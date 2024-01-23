package io.dock2dock.android.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skydoves.sandwich.map
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.onSuccess
import io.dock2dock.android.ApiService
import io.dock2dock.android.SERVER_NETWORK_ERROR
import io.dock2dock.android.UNAUTHORISED_NETWORK_ERROR
import io.dock2dock.android.clients.PublicApiClient
import io.dock2dock.android.configuration.Dock2DockConfiguration
import io.dock2dock.android.models.Dock2DockErrorCode
import io.dock2dock.android.models.HttpErrorMapper
import io.dock2dock.android.models.commands.CreateCrossdockLabel
import io.dock2dock.android.models.query.CrossdockHandlingUnit
import io.dock2dock.android.models.query.Printer
import kotlinx.coroutines.launch

internal class DialogPrintCrossdockLabelViewModel(val onSuccess: () -> Unit,
                                         val salesOrderNo: String): ViewModel()
{
    private val publicApiClient = ApiService.getRetrofitClient<PublicApiClient>()

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _loadError = MutableLiveData("")
    val loadError: LiveData<String> = _loadError
    private fun onLoadErrorChange(value: String) {
        _loadError.value = value
    }

    var handlingUnit by mutableStateOf<CrossdockHandlingUnit?>(null)
    var handlingUnitName by mutableStateOf<String>("")

    var quantity by mutableStateOf(0)
        private set
    var printer by mutableStateOf<Printer?>(null)
    var printerName by mutableStateOf<String>("")

    var printers by mutableStateOf(listOf<Printer>())

    var handlingUnits by mutableStateOf(listOf<CrossdockHandlingUnit>())

    private val dock2dockConfiguration = Dock2DockConfiguration.instance()

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

    fun onHandlingUnitValueChanged(value: CrossdockHandlingUnit) {
        handlingUnit = value
        handlingUnitName = value.name
        validateHandlingUnitId()
    }

    fun onPrinterValueChanged(value: Printer) {
        printer = value
        printerName = value.name
        validatePrinterId()
    }

    //validate
    private fun validateQuantity() {
        val result = quantity > 0
        quantityIsError = !result
    }

    private fun validateHandlingUnitId() {
        handlingUnitIdIsError = handlingUnit?.id.isNullOrEmpty()
    }

    private fun validatePrinterId() {
        printerIdIsError =  printer?.id.isNullOrEmpty()
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

                var printerId = dock2dockConfiguration.getDefaultPrinter()
                if (!printerId.isNullOrEmpty()) {
                    printer = printers.firstOrNull { it.id == printerId }
                    printerName = printer?.name ?: ""
                }
            }.onError {
                map(HttpErrorMapper) {
                    when(this.code) {
                        Dock2DockErrorCode.Unauthorised -> onLoadErrorChange(UNAUTHORISED_NETWORK_ERROR)
                        else -> {
                            onLoadErrorChange(SERVER_NETWORK_ERROR)
                        }
                    }
                }
            }.onException {
                onLoadErrorChange(SERVER_NETWORK_ERROR)
            }
        }
    }

    private fun getHandlingUnits() {
        viewModelScope.launch {
            val response = publicApiClient.getCrossdockHandlingUnits()
            response.onSuccess {
                handlingUnits = this.data.value

                var handlingUnitId = dock2dockConfiguration.getDefaultHandlingUnit()
                if (!handlingUnitId.isNullOrEmpty()) {
                    handlingUnit = handlingUnits.firstOrNull { it.id == handlingUnitId }
                    handlingUnitName = handlingUnit?.name ?: ""
                }
            }.onError {
                map(HttpErrorMapper) {
                    when(this.code) {
                        Dock2DockErrorCode.Unauthorised -> onLoadErrorChange(UNAUTHORISED_NETWORK_ERROR)
                        else -> {
                            onLoadErrorChange(SERVER_NETWORK_ERROR)
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
            val createCrossdockLabel = CreateCrossdockLabel(salesOrderNo, handlingUnit?.id ?: "", quantity, printer?.id ?: "")
            val response = publicApiClient.createCrossdockLabel(createCrossdockLabel)
            response.onSuccess {
                onLoadErrorChange("")
                onSuccess()
            }.onError {
                map(HttpErrorMapper) {
                    when(this.code) {
                        Dock2DockErrorCode.Unauthorised -> onLoadErrorChange(UNAUTHORISED_NETWORK_ERROR)
                        Dock2DockErrorCode.BadRequest,
                        Dock2DockErrorCode.UnprocessableEntity,
                        Dock2DockErrorCode.NotFound,
                        Dock2DockErrorCode.Validation -> onLoadErrorChange(this.message)
                        else -> {
                            onLoadErrorChange(SERVER_NETWORK_ERROR)
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