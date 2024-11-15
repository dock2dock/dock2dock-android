package io.dock2dock.android.crossdock.viewModels

import androidx.lifecycle.viewModelScope
import com.skydoves.sandwich.map
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.onSuccess
import io.dock2dock.android.application.configuration.Dock2DockConfiguration
import io.dock2dock.android.application.eventBus.Dock2DockEventBus
import io.dock2dock.android.application.events.Dock2DockShowLPQuickCreateViewChangedEvent
import io.dock2dock.android.application.events.LicensePlateSetToActiveEvent
import io.dock2dock.android.application.models.commands.CompleteLicensePlateRequest
import io.dock2dock.android.application.models.commands.ReprintLicensePlateRequest
import io.dock2dock.android.application.models.query.LicensePlate
import io.dock2dock.android.networking.ApiService
import io.dock2dock.android.networking.SERVER_NETWORK_ERROR
import io.dock2dock.android.networking.UNAUTHORISED_NETWORK_ERROR
import io.dock2dock.android.networking.clients.PublicApiClient
import io.dock2dock.android.networking.models.Dock2DockErrorCode
import io.dock2dock.android.networking.models.HttpErrorMapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LicensePlateViewModel(val salesOrderNo: String): BaseViewModel()
{
    var lpSettingsViewModel: LicensePlateQuickCreateViewModel
    private val dock2dockConfiguration = Dock2DockConfiguration.instance()
    private var _showLPQuickCreateView = MutableStateFlow(false)

    val showLPQuickCreateView: StateFlow<Boolean>
        get() = _showLPQuickCreateView

    init {
        subscribeToActiveLicensePlateUpdatedEvent()
        subscribeToShowLpQuickCreateViewUpdatedEvent()
        lpSettingsViewModel = LicensePlateQuickCreateViewModel(salesOrderNo)
        _showLPQuickCreateView.value = dock2dockConfiguration.getShowLPQuickCreateViewSetting()
    }

    private val publicApiClient = ApiService.getRetrofitClient<PublicApiClient>()

    private val _licensePlate = MutableStateFlow<LicensePlate?>(null)

    private val refreshing = MutableStateFlow(false)

    private val errorMessage = MutableStateFlow<String?>(null)

    val licensePlate: StateFlow<LicensePlate?>
        get() = _licensePlate

    var onLicensePlateChanged: (LicensePlate?) -> Unit = {}

    private fun getLicensePlate(barcode: String) {
        errorMessage.value = ""
        viewModelScope.launch {
            refreshing.value = true
            val response = publicApiClient.getLicensePlate(barcode)
            response.onSuccess {
                _licensePlate.value = this.data
                onLicensePlateChanged(_licensePlate.value)
            }.onError {
                map(HttpErrorMapper) {
                    when(this.code) {
                        Dock2DockErrorCode.Unauthorised -> {
                            errorMessage.value = UNAUTHORISED_NETWORK_ERROR
                        }
                        Dock2DockErrorCode.NotFound -> {
                            errorMessage.value = this.message
                        }
                        else -> {
                            errorMessage.value = SERVER_NETWORK_ERROR
                        }
                    }
                }
            }.onException {
                errorMessage.value = SERVER_NETWORK_ERROR
            }
            refreshing.value = false
        }
    }

    fun refresh() {
        _licensePlate.value?.let {
            getLicensePlate(it.no)
        }
    }

    fun load(barcode: String) {
        getLicensePlate(barcode)
    }

    fun clearLicensePlate() {
        _licensePlate.value = null
        onLicensePlateChanged(null)
    }

    fun complete() {
        val defaultPrinterId = dock2dockConfiguration.getDefaultPrinter()?.let { it } ?: run {
            errorMessage.value = "Please select default printer under Dock2Dock settings"
            return@complete
        }

        viewModelScope.launch {
            val request = CompleteLicensePlateRequest(licensePlate.value?.no ?: "",  defaultPrinterId)
            val response = publicApiClient.completeLicensePlate(request)
            response.onSuccess {
                clearLicensePlate()
            }.onError {
                map(HttpErrorMapper) {
                    when(this.code) {
                        Dock2DockErrorCode.Unauthorised -> errorMessage.value = UNAUTHORISED_NETWORK_ERROR
                        else -> {
                            errorMessage.value = SERVER_NETWORK_ERROR
                        }
                    }
                }
            }.onException {
                errorMessage.value = "An error has occurred. Please retry or contact Dock2Dock support team."
            }
            //_isLoading.value = false
        }
    }


    fun reprint(printSsccBarcode: Boolean) {
        val defaultPrinterId = dock2dockConfiguration.getDefaultPrinter()?.let { it } ?: run {
            errorMessage.value = "Please select default printer under Dock2Dock settings"
            return@reprint
        }

        viewModelScope.launch {
            val licensePlateNo = licensePlate.value?.no ?: ""
            val request = ReprintLicensePlateRequest(licensePlateNo,  defaultPrinterId, printSsccBarcode)
            val response = publicApiClient.reprintLicensePlate(request)
            response.onSuccess {

            }.onError {
                map(HttpErrorMapper) {
                    when(this.code) {
                        Dock2DockErrorCode.Unauthorised -> errorMessage.value = UNAUTHORISED_NETWORK_ERROR
                        else -> {
                            errorMessage.value = SERVER_NETWORK_ERROR
                        }
                    }
                }
            }.onException {
                errorMessage.value = "An error has occurred. Please retry or contact Dock2Dock support team."
            }
            //_isLoading.value = false
        }
    }

    private fun subscribeToActiveLicensePlateUpdatedEvent() {
        viewModelScope.launch {
            Dock2DockEventBus.subscribe<LicensePlateSetToActiveEvent> { event ->
                load(event.licensePlateNo)
            }
        }
    }

    private fun subscribeToShowLpQuickCreateViewUpdatedEvent() {
        viewModelScope.launch {
            Dock2DockEventBus.subscribe<Dock2DockShowLPQuickCreateViewChangedEvent> { event ->
                _showLPQuickCreateView.value = event.value
            }
        }
    }
}
