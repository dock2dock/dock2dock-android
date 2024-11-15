package io.dock2dock.android.freightmanagement.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skydoves.sandwich.map
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.onSuccess
import io.dock2dock.android.application.configuration.Dock2DockConfiguration
import io.dock2dock.android.application.eventBus.Dock2DockEventBus
import io.dock2dock.android.application.events.Dock2DockBarcodeScannedEvent
import io.dock2dock.android.application.models.commands.AddPackageToShippingContainerRequest
import io.dock2dock.android.application.models.commands.CompleteShippingContainerRequest
import io.dock2dock.android.application.models.commands.RemovePackageFromShippingContainerRequest
import io.dock2dock.android.application.models.commands.ReprintShippingContainerRequest
import io.dock2dock.android.application.models.query.ShippingContainerPackage
import io.dock2dock.android.application.models.query.ShippingContainer
import io.dock2dock.android.networking.ApiService
import io.dock2dock.android.networking.SERVER_NETWORK_ERROR
import io.dock2dock.android.networking.UNAUTHORISED_NETWORK_ERROR
import io.dock2dock.android.networking.clients.PublicApiClient
import io.dock2dock.android.networking.models.Dock2DockErrorCode
import io.dock2dock.android.networking.models.HttpErrorMapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date

class ShippingContainerViewModel(private val shippingContainerId: String, private val onBackCallback: (() -> Boolean)): ViewModel() {
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading
    private fun onIsLoadingChange(value: Boolean) {
        _isLoading.value = value
    }

    private val dock2dockConfiguration = Dock2DockConfiguration.instance()

    private val publicApiClient = ApiService.getRetrofitClient<PublicApiClient>()

    private var _deleteMode = MutableStateFlow(false)

    val deleteMode: StateFlow<Boolean>
        get() = _deleteMode

    fun onDeleteModeChanged() {
        _deleteMode.value = !_deleteMode.value
    }

    private var _shippingContainer = MutableStateFlow<ShippingContainer?>(null)

    val shippingContainer: StateFlow<ShippingContainer?>
        get() = _shippingContainer

    private val _shippingContainerPackages = MutableStateFlow<List<ShippingContainerPackage>>(listOf())

    val shippingContainerPackages: StateFlow<List<ShippingContainerPackage>>
        get() = _shippingContainerPackages

    fun onCloseErrorDialog() {
        _errorMessage.value = ""
    }

    private val _errorMessage = MutableStateFlow("")

    val errorMessage: StateFlow<String>
        get() = _errorMessage

    init {
        subscribeToBarcodeScannedEvent()
        load()
    }

    private fun getShippingContainer() {
        viewModelScope.launch {
            onIsLoadingChange(true)
            val response = publicApiClient.getShippingContainer(shippingContainerId)
            response.onSuccess {
                _shippingContainer.value = this.data
                _shippingContainerPackages.value = this.data.shippingContainerPackages
            }.onError {
                map(HttpErrorMapper) {
                    when(this.code) {
                        Dock2DockErrorCode.Unauthorised -> {
                            _errorMessage.value = UNAUTHORISED_NETWORK_ERROR
                        }
                        Dock2DockErrorCode.NotFound -> {
                            _errorMessage.value = this.message
                        }
                        else -> {
                            _errorMessage.value = SERVER_NETWORK_ERROR
                        }
                    }
                }
            }.onException {
                _errorMessage.value = SERVER_NETWORK_ERROR
            }
            onIsLoadingChange(false)
        }
    }

    private fun load() {
        getShippingContainer()
    }

    fun refresh() {
        getShippingContainer()
    }

    fun complete() {
        viewModelScope.launch {
            val printerId = dock2dockConfiguration.getDefaultPrinter() ?: ""

            if (printerId.isEmpty()) {
                _errorMessage.value = "Please select default printer in settings to continue"
                return@launch
            }
            onIsLoadingChange(true)
            val request = CompleteShippingContainerRequest(shippingContainerId, printerId)
            val response = publicApiClient.completeShippingContainer(request)
            response.onSuccess {
                onBackCallback()
            }.onError {
                map(HttpErrorMapper) {
                    when(this.code) {
                        Dock2DockErrorCode.Unauthorised -> {
                            _errorMessage.value = UNAUTHORISED_NETWORK_ERROR
                        }
                        Dock2DockErrorCode.BadRequest,
                        Dock2DockErrorCode.Validation,
                        Dock2DockErrorCode.NotFound -> {
                            _errorMessage.value = this.message
                        }
                        else -> {
                            _errorMessage.value = SERVER_NETWORK_ERROR
                        }
                    }
                }
            }.onException {
                _errorMessage.value = SERVER_NETWORK_ERROR
            }
            onIsLoadingChange(false)
        }
    }

    fun reprint() {
        _errorMessage.value = ""
        viewModelScope.launch {
            val printerId = dock2dockConfiguration.getDefaultPrinter() ?: ""

            if (printerId.isEmpty()) {
                _errorMessage.value = "Please select default printer in settings to continue"
                return@launch
            }
            onIsLoadingChange(true)
            val request= ReprintShippingContainerRequest(shippingContainerId, printerId)
            val response = publicApiClient.reprintShippingContainer(request)
            response.onSuccess {

            }.onError {
                map(HttpErrorMapper) {
                    when(this.code) {
                        Dock2DockErrorCode.Unauthorised -> {
                            _errorMessage.value = UNAUTHORISED_NETWORK_ERROR
                        }
                        else -> {
                            _errorMessage.value = SERVER_NETWORK_ERROR
                        }
                    }
                }
            }.onException {
                _errorMessage.value = SERVER_NETWORK_ERROR
            }
            onIsLoadingChange(false)
        }
    }

    private fun addPackage(barcode: String) {
        viewModelScope.launch {
            onIsLoadingChange(true)
            val request = AddPackageToShippingContainerRequest(shippingContainerId, barcode)
            val response = publicApiClient.addPackageToShippingContainer(request)
            response.onSuccess {
                val shippingContainerPackage = ShippingContainerPackage(
                    "",
                    this.data.consignmentPackageBarcode,
                    this.data.customerName,
                    this.data.consignmentProductName,
                    this.data.consignmentHeaderNo,
                    this.data.description,
                    this.data.quantity,
                    false,
                    Date()
                )
                _shippingContainerPackages.value += shippingContainerPackage
                updateShippingContainerQty(1, shippingContainerPackage.quantity)
            }.onError {
                map(HttpErrorMapper) {
                    when(this.code) {
                        Dock2DockErrorCode.Unauthorised -> {
                            _errorMessage.value = UNAUTHORISED_NETWORK_ERROR
                        }
                        Dock2DockErrorCode.BadRequest,
                        Dock2DockErrorCode.Validation,
                        Dock2DockErrorCode.NotFound -> {
                            _errorMessage.value = this.message
                        }
                        else -> {
                            _errorMessage.value = SERVER_NETWORK_ERROR
                        }
                    }
                }
            }.onException {
                _errorMessage.value = SERVER_NETWORK_ERROR
            }
            onIsLoadingChange(false)
        }
    }

    private fun removePackage(barcode: String) {
        viewModelScope.launch {
            onIsLoadingChange(true)
            val request = RemovePackageFromShippingContainerRequest(shippingContainerId, barcode)
            val response = publicApiClient.removePackageFromShippingContainer(request)
            response.onSuccess {
                val consignmentPackage = shippingContainerPackages.value.firstOrNull { it.barcode == barcode }

                if (consignmentPackage != null) {
                    _shippingContainerPackages.value -= consignmentPackage
                    updateShippingContainerQty(-1, -consignmentPackage.quantity)
                }
            }.onError {
                map(HttpErrorMapper) {
                    when(this.code) {
                        Dock2DockErrorCode.Unauthorised -> {
                            _errorMessage.value = UNAUTHORISED_NETWORK_ERROR
                        }
                        Dock2DockErrorCode.BadRequest,
                        Dock2DockErrorCode.Validation,
                        Dock2DockErrorCode.NotFound -> {
                            _errorMessage.value = this.message
                        }
                        else -> {
                            _errorMessage.value = SERVER_NETWORK_ERROR
                        }
                    }
                }
            }.onException {
                _errorMessage.value = SERVER_NETWORK_ERROR
            }
            onIsLoadingChange(false)
        }
    }

    private fun updateShippingContainerQty(quantity: Int, weight: Double) {
        val existingQty = shippingContainer.value?.quantity ?: 0
        val existingWeight  = shippingContainer.value?.weight ?: 0.0
        _shippingContainer.value = _shippingContainer.value?.copy(
            weight = existingWeight + weight,
            quantity = existingQty + quantity
        )

        shippingContainer.value?.let {
            if (it.quantity <= 0) {
                _deleteMode.value = false
            }
        }
    }

    //region Barcode

    private fun subscribeToBarcodeScannedEvent() {
        viewModelScope.launch {
            Dock2DockEventBus.subscribe<Dock2DockBarcodeScannedEvent> { event ->
                if (deleteMode.value) {
                    removePackage(event.barcode)
                } else {
                    addPackage(event.barcode)
                }
            }
        }
    }

    //endregion
}