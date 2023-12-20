package io.dock2dock.android.viewModels

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
import io.dock2dock.android.eventBus.Dock2DockEventBus
import io.dock2dock.android.events.LicensePlateSetToActiveEvent
import io.dock2dock.android.models.Dock2DockErrorCode
import io.dock2dock.android.models.HttpErrorMapper
import io.dock2dock.android.models.commands.CompleteLicensePlateRequest
import io.dock2dock.android.models.commands.ReprintLicensePlateRequest
import io.dock2dock.android.models.query.LicensePlate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LicensePlateViewModel : ViewModel()
{
    init {
        subscribeToActiveLicensePlateUpdatedEvent()
    }

    private val publicApiClient = ApiService.getRetrofitClient<PublicApiClient>()

    private val _licensePlate = MutableStateFlow<LicensePlate?>(null)

    private val refreshing = MutableStateFlow(false)

    private val errorMessage = MutableStateFlow<String?>(null)

    private val dock2dockConfiguration = Dock2DockConfiguration.instance()

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

    fun refresh(barcode: String) {
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
            var request = CompleteLicensePlateRequest(licensePlate.value?.no ?: "",  defaultPrinterId)
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
            var request = ReprintLicensePlateRequest(licensePlate.value?.no ?: "",  defaultPrinterId, printSsccBarcode)
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
                refresh(event.licensePlateNo)
            }
        }
    }
}
