package com.dock2dock.android.crossdock.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dock2dock.android.application.models.commands.CreateCrossdockLabel
import com.dock2dock.android.application.models.query.CrossdockHandlingUnit
import com.dock2dock.android.application.models.query.Printer
import com.dock2dock.android.networking.ApiService
import com.dock2dock.android.networking.clients.PublicApiClient
import com.dock2dock.android.networking.configuration.Dock2DockConfiguration
import com.dock2dock.android.networking.managers.TokenManager
import com.dock2dock.android.networking.utilities.Constants
import com.skydoves.sandwich.StatusCode
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.onSuccess
import kotlinx.coroutines.launch

internal class DialogPrintCrossdockLabelViewModel(tokenManager: TokenManager,
                                                  dock2DockConfiguration: Dock2DockConfiguration,
                                                  val salesOrderId: String): ViewModel()
{
    private val publicApiClient = ApiService.getRetrofitClient<PublicApiClient>(
        tokenManager,
        dock2DockConfiguration,
        Constants.PUBLICAPI_BASEURL
    )

    var isLoading = mutableStateOf(false)
    var loadError = mutableStateOf("")

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

            }.onException {

            }
        }
    }

    private fun getHandlingUnits() {
        viewModelScope.launch {
            var response = publicApiClient.getCrossdockHandlingUnits()
            response.onSuccess {
                handlingUnits = this.data.value
            }.onError {

            }.onException {

            }
        }
    }

    fun submit(onSuccessAction: ()-> Unit) {
        if (!validateForm()) {
            return
        }
        viewModelScope.launch {
            //isLoading.value = true
            var createCrossdockLabel = CreateCrossdockLabel(salesOrderId, handlingUnitId, quantity, printerId)
            var response = publicApiClient.createCrossdockLabel(createCrossdockLabel)
            response.onSuccess {
                loadError.value = ""
                onSuccessAction()
            }.onError {
                when(this.statusCode) {
                    StatusCode.Unauthorized -> loadError.value = "We couldn't validate your credentials. Please check before continuing."
                    else -> {
                        loadError.value = "An error has occurred. Please retry or contact Dock2Dock support team."
                    }
                }
//                map(HttpErrorMapper) {
//                    val code = this.code
//                    val message = this.message
//                }
            }.onException {
                loadError.value = "An error has occurred. Please retry or contact Dock2Dock support team."
            }

            isLoading.value = false
        }
    }
}