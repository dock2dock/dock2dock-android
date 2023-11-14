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
import io.dock2dock.android.models.commands.ReprintCrossdockLabel
import io.dock2dock.android.models.query.CrossdockLabel
import io.dock2dock.android.models.query.Printer
import kotlinx.coroutines.launch

internal class ReprintCrossdockLabelViewModel(val onSuccess: () -> Unit,
                                              val crossdockLabel: CrossdockLabel): ViewModel()
{
    private val publicApiClient = ApiService.getRetrofitClient<PublicApiClient>()

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _loadError = MutableLiveData("")
    val loadError: LiveData<String> = _loadError
    private fun onLoadErrorChange(value: String) {
        _loadError.value = value
    }

    var printer by mutableStateOf<Printer?>(null)
    var printerName by mutableStateOf<String>("")

    var printers by mutableStateOf(listOf<Printer>())

    private val dock2dockConfiguration = Dock2DockConfiguration.instance()

    fun load() {
        getPrinters()
    }

    var printerIdIsError by mutableStateOf(false)
        private set

    //validation message
    val printerIdErrorMessage = "Printer is a required field"

    fun onPrinterValueChanged(value: Printer) {
        printer = value
        printerName = value.name
        validatePrinterId()
    }

    //validate

    private fun validatePrinterId() {
        printerIdIsError =  printer?.id.isNullOrEmpty()
    }

    private fun validateForm(): Boolean {
        validatePrinterId()

        return !printerIdIsError
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

    fun onSubmit() {
        if (!validateForm()) {
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            val command = ReprintCrossdockLabel(crossdockLabel.id, printer?.id ?: "")
            val response = publicApiClient.reprintCrossdockLabel(command)
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