package io.dock2dock.crossdock.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.dock2dock.application.models.commands.CreateCrossdockLabel
import io.dock2dock.application.models.query.CrossdockHandlingUnit
import io.dock2dock.application.models.query.Printer
import io.dock2dock.networking.ApiService
import io.dock2dock.networking.clients.PublicApiClient
import io.dock2dock.networking.configuration.Dock2DockConfiguration
import io.dock2dock.networking.managers.TokenManager
import io.dock2dock.networking.models.Dock2DockErrorCode
import io.dock2dock.networking.models.HttpErrorMapper
import io.dock2dock.networking.utilities.Constants
import com.skydoves.sandwich.*
import kotlinx.coroutines.launch

class DialogPrintCrossdockLabelViewModel(dock2DockConfiguration: Dock2DockConfiguration,
                                         val onSuccess: () -> Unit,
                                         val salesOrderNo: String): ViewModel()
{
    private val publicApiClient = ApiService.getRetrofitClient<PublicApiClient>(
        dock2DockConfiguration,
        Constants.PUBLICAPI_BASEURL
    )

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _loadError = MutableLiveData("")
    val loadError: LiveData<String> = _loadError
    private fun onLoadErrorChange(value: String) {
        _loadError.value = value
    }

    var handlingUnitId by mutableStateOf("")
        private set
    var quantity by mutableStateOf(0)
        private set
    var printerId by mutableStateOf("")
        private set

    var printers by mutableStateOf(listOf<Printer>())

    var handlingUnits by mutableStateOf(listOf<CrossdockHandlingUnit>())

    init {
        getHandlingUnits()
        getPrinters()
    }

    //isError
    var quantityIsError by mutableStateOf(false)
        private set

    var handlingUnitIdIsError by mutableStateOf(false)
        private set

    var printerIdIsError by mutableStateOf(false)
        private set

    //validation message
    val quantityValidationMessage = "Quantity must be greater than 0"
    val handlingUnitIdErrorMessage = "Handling Unit is a required field"
    val printerIdErrorMessage = "Printer is a required field"

    //value changed
    fun onQuantityValueChanged(value: Int) {
        quantity = value
        validateQuantity()
    }

    fun onHandlingUnitIdValueChanged(value: String) {
        handlingUnitId = value
        validateHandlingUnitId()
    }

    fun onPrinterIdValueChanged(value: String) {
        printerId = value
        validatePrinterId()
    }

    //validate
    private fun validateQuantity() {
        val result = quantity > 0
        quantityIsError = !result
    }

    private fun validateHandlingUnitId() {
        handlingUnitIdIsError = handlingUnitId.isNullOrEmpty()
    }

    private fun validatePrinterId() {
        printerIdIsError =  printerId.isNullOrEmpty()
    }

    private fun validateForm(): Boolean {
        validateQuantity()
        validatePrinterId()
        validateHandlingUnitId()

        return !quantityIsError && !handlingUnitIdIsError && !printerIdIsError
    }

    private fun getPrinters() {
        viewModelScope.launch {
            val response = publicApiClient.getPrinters("Name asc")
            response.onSuccess {
                printers = this.data.value
            }.onError {
                map(HttpErrorMapper) {
                    when(this.code) {
                        Dock2DockErrorCode.Unauthorised -> onLoadErrorChange("We couldn't validate your credentials. Please check before continuing.")
                        else -> {
                            onLoadErrorChange("An error has occurred. Please retry or contact Dock2Dock support team.")
                        }
                    }
                }
            }.onException {
                onLoadErrorChange("An error has occurred. Please retry or contact Dock2Dock support team.")
            }
        }
    }

    private fun getHandlingUnits() {
        viewModelScope.launch {
            val response = publicApiClient.getCrossdockHandlingUnits()
            response.onSuccess {
                handlingUnits = this.data.value
            }.onError {
                map(HttpErrorMapper) {
                    when(this.code) {
                        Dock2DockErrorCode.Unauthorised -> onLoadErrorChange("We couldn't validate your credentials. Please check before continuing.")
                        else -> {
                            onLoadErrorChange("An error has occurred. Please retry or contact Dock2Dock support team.")
                        }
                    }
                }
            }.onException {
                onLoadErrorChange("An error has occurred. Please retry or contact Dock2Dock support team.")
            }
        }
    }

    fun onSubmit() {
        if (!validateForm()) {
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            val createCrossdockLabel = CreateCrossdockLabel(salesOrderNo, handlingUnitId, quantity, printerId)
            val response = publicApiClient.createCrossdockLabel(createCrossdockLabel)
            response.onSuccess {
                onLoadErrorChange("")
                onSuccess()
            }.onError {
                map(HttpErrorMapper) {
                    when(this.code) {
                        Dock2DockErrorCode.Unauthorised -> onLoadErrorChange("We couldn't validate your credentials. Please check before continuing.")
                        else -> {
                            onLoadErrorChange("An error has occurred. Please retry or contact Dock2Dock support team.")
                        }
                    }
                }
            }.onException {
                onLoadErrorChange("An error has occurred. Please retry or contact Dock2Dock support team.")
            }

            _isLoading.value = false
        }
    }
}