package com.dock2dock.android.crossdock.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dock2dock.android.application.models.commands.CreateCrossdockLabel
import com.dock2dock.android.application.models.query.CrossdockHandlingUnit
import com.dock2dock.android.application.models.query.Printer
import com.dock2dock.android.networking.ApiService
import com.dock2dock.android.networking.clients.PublicApiClient
import com.dock2dock.android.networking.configuration.Dock2DockConfiguration
import com.dock2dock.android.networking.managers.TokenManager
import com.dock2dock.android.networking.models.Dock2DockErrorCode
import com.dock2dock.android.networking.models.HttpErrorMapper
import com.dock2dock.android.networking.utilities.Constants
import com.skydoves.sandwich.*
import kotlinx.coroutines.launch

class DialogPrintCrossdockLabelViewModel(tokenManager: TokenManager,
                                         dock2DockConfiguration: Dock2DockConfiguration,
                                         val onSuccess: () -> Unit,
                                         val salesOrderNo: String): ViewModel()
{
    private val publicApiClient = ApiService.getRetrofitClient<PublicApiClient>(
        tokenManager,
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
    var printerId by mutableStateOf("Test")
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
        var result = quantity > 0
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
            var response = publicApiClient.getPrinters()
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
            var response = publicApiClient.getCrossdockHandlingUnits()
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
            var createCrossdockLabel = CreateCrossdockLabel(salesOrderNo, handlingUnitId, quantity, printerId)
            var response = publicApiClient.createCrossdockLabel(createCrossdockLabel)
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