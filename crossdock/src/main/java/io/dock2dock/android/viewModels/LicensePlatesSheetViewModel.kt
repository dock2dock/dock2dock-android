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
import io.dock2dock.android.models.Dock2DockErrorCode
import io.dock2dock.android.models.HttpErrorMapper
import io.dock2dock.android.models.commands.ReprintLicensePlateRequest
import io.dock2dock.android.models.query.LicensePlate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LicensePlatesSheetViewModel(
    val salesOrderNo: String,
    val onLicensePlateSetActive: ((LicensePlate) -> Unit) = {}
) : ViewModel()
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

    internal fun load() {
        getLicensePlates()
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
