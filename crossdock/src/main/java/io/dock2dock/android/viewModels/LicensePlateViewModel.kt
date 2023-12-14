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
import io.dock2dock.android.models.Dock2DockErrorCode
import io.dock2dock.android.models.HttpErrorMapper
import io.dock2dock.android.models.query.LicensePlate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LicensePlateViewModel : ViewModel()
{
    private val publicApiClient = ApiService.getRetrofitClient<PublicApiClient>()

    private val _licensePlate = MutableStateFlow<LicensePlate?>(null)

    val refreshing = MutableStateFlow(false)

    private val errorMessage = MutableStateFlow<String?>(null)

    val licensePlate: StateFlow<LicensePlate?>
        get() = _licensePlate

    private fun getLicensePlate(barcode: String) {
        errorMessage.value = ""
        viewModelScope.launch {
            refreshing.value = true
            val response = publicApiClient.getLicensePlate(barcode)
            response.onSuccess {
                _licensePlate.value = this.data
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
    }
}
