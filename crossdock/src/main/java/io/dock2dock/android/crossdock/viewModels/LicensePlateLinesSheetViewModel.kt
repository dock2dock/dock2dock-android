package io.dock2dock.android.crossdock.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skydoves.sandwich.map
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.onSuccess
import io.dock2dock.android.application.models.query.LicensePlateLine
import io.dock2dock.android.crossdock.SERVER_NETWORK_ERROR
import io.dock2dock.android.crossdock.UNAUTHORISED_NETWORK_ERROR
import io.dock2dock.android.networking.ApiService
import io.dock2dock.android.networking.clients.PublicApiClient
import io.dock2dock.android.networking.models.Dock2DockErrorCode
import io.dock2dock.android.networking.models.HttpErrorMapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LicensePlateLinesSheetViewModel: ViewModel() {
    private val publicApiClient = ApiService.getRetrofitClient<PublicApiClient>()

    private val _licensePlateLines = MutableStateFlow<List<LicensePlateLine>>(listOf())

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean>
        get() = _isLoading

    private val errorMessage = MutableStateFlow<String?>(null)

    val licensePlateLines: StateFlow<List<LicensePlateLine>>
        get() = _licensePlateLines

    private fun getLicensePlateLines(licensePlateNo: String) {
        errorMessage.value = ""
        _licensePlateLines.value = listOf()
        viewModelScope.launch {
            _isLoading.value = true
            val response =
                publicApiClient.getLicensePlateLines("LicensePlateNo eq '$licensePlateNo'", "No")
            response.onSuccess {
                _licensePlateLines.value = this.data.value
            }.onError {
                map(HttpErrorMapper) {
                    when (this.code) {
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
            _isLoading.value = false
        }
    }

    internal fun load(licensePlateNo: String) {
        getLicensePlateLines(licensePlateNo)
    }
}