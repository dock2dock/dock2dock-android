package io.dock2dock.android.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
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
import io.dock2dock.android.models.query.LicensePlateLine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LicensePlateLinesSheetViewModelFactory(private val licensePlateId: String) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LicensePlateLinesSheetViewModel::class.java)) {
            return LicensePlateLinesSheetViewModel(licensePlateId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class LicensePlateLinesSheetViewModel(
    private val licensePlateNo: String): ViewModel() {
    private val publicApiClient = ApiService.getRetrofitClient<PublicApiClient>()

    private val _licensePlateLines = MutableStateFlow<List<LicensePlateLine>>(listOf())

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean>
        get() = _isLoading

    private val errorMessage = MutableStateFlow<String?>(null)

    val licensePlateLines: StateFlow<List<LicensePlateLine>>
        get() = _licensePlateLines

    private fun getLicensePlateLines() {
        errorMessage.value = ""
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

    internal fun load() {
        getLicensePlateLines()
    }
}