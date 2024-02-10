package io.dock2dock.android.crossdock.viewModels

import androidx.lifecycle.viewModelScope
import com.skydoves.sandwich.map
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.onSuccess
import io.dock2dock.android.application.configuration.Dock2DockConfiguration
import io.dock2dock.android.application.models.commands.ReprintLicensePlateRequest
import io.dock2dock.android.application.models.query.LicensePlate
import io.dock2dock.android.crossdock.SERVER_NETWORK_ERROR
import io.dock2dock.android.crossdock.UNAUTHORISED_NETWORK_ERROR
import io.dock2dock.android.networking.ApiService
import io.dock2dock.android.networking.clients.PublicApiClient
import io.dock2dock.android.networking.models.Dock2DockErrorCode
import io.dock2dock.android.networking.models.HttpErrorMapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LicensePlatesSheetViewModel(
    val salesOrderNo: String,
    val onLicensePlateSetActive: ((LicensePlate) -> Unit) = {}
): BaseViewModel()
{
    private val publicApiClient = ApiService.getRetrofitClient<PublicApiClient>()

    private val _licensePlates = MutableStateFlow<List<LicensePlate>>(listOf())

    val refreshing = MutableStateFlow(false)

    private val errorMessage = MutableStateFlow<String?>(null)

    private val dock2dockConfiguration = Dock2DockConfiguration.instance()

    val licensePlates: StateFlow<List<LicensePlate>>
        get() = _licensePlates

    private val _selectedItem: MutableStateFlow<LicensePlate?> = MutableStateFlow(null)
    val selectedItem: StateFlow<LicensePlate?> get() = _selectedItem



    private fun getLicensePlates() {
        errorMessage.value = ""
        viewModelScope.launch {
            refreshing.value = true
            val response = publicApiClient.getLicensePlates("SalesOrderNo eq '$salesOrderNo'", "DateCreated desc")
            response.onSuccess {
                _licensePlates.value = this.data.value
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

    internal fun refresh() {
        getLicensePlates()
    }

    internal fun setSelectedItem(item: LicensePlate?) {
        _selectedItem.value = item
    }

    internal fun setActive(item: LicensePlate) {
        onLicensePlateSetActive(item)
    }

    internal fun reprintLicensePlate(licensePlate: LicensePlate, printSsccBarcode: Boolean) {
        val defaultPrinterId = dock2dockConfiguration.getDefaultPrinter()?.let { it } ?: run {
            errorMessage.value = "Please select default printer under Dock2Dock settings"
            return@reprintLicensePlate
        }

        viewModelScope.launch {
            var request = ReprintLicensePlateRequest(licensePlate.no,  defaultPrinterId, printSsccBarcode)
            val response = publicApiClient.reprintLicensePlate(request)
            response.onSuccess {
                _successMessage.value = if (printSsccBarcode) {
                    "License plate ${licensePlate.no} and SSCC label have been reprinted"
                } else {
                    "License plate ${licensePlate.no} has been reprinted"
                }
                _isSnackBarShowing.value = true
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
}
