package io.dock2dock.android.freightmanagement.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skydoves.sandwich.map
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.onSuccess
import io.dock2dock.android.application.configuration.Dock2DockConfiguration
import io.dock2dock.android.application.models.query.FreightConsignmentManifest
import io.dock2dock.android.networking.ApiService
import io.dock2dock.android.networking.SERVER_NETWORK_ERROR
import io.dock2dock.android.networking.UNAUTHORISED_NETWORK_ERROR
import io.dock2dock.android.networking.clients.PublicApiClient
import io.dock2dock.android.networking.models.Dock2DockErrorCode
import io.dock2dock.android.networking.models.HttpErrorMapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

internal class ConsignmentManifestScreenViewModel(private val consignmentManifestId: String): ViewModel() {
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading
    private fun onIsLoadingChange(value: Boolean) {
        _isLoading.value = value
    }

    private val dock2dockConfiguration = Dock2DockConfiguration.instance()

    private val publicApiClient = ApiService.getRetrofitClient<PublicApiClient>()

    private var _deleteMode = MutableStateFlow(false)

    val deleteMode: StateFlow<Boolean>
        get() = _deleteMode

    fun onDeleteModeChanged() {
        _deleteMode.value = !_deleteMode.value
    }

    private var _consignmentManifest = MutableStateFlow<FreightConsignmentManifest?>(null)

    val consignmentManifest: StateFlow<FreightConsignmentManifest?>
        get() = _consignmentManifest

    fun onCloseErrorDialog() {
        _errorMessage.value = ""
    }

    private val _errorMessage = MutableStateFlow("")

    val errorMessage: StateFlow<String>
        get() = _errorMessage

    init {
        load()
    }

    private fun getConsignmentManifest() {
        viewModelScope.launch {
            onIsLoadingChange(true)
            val response = publicApiClient.getConsignmentManifest(consignmentManifestId)
            response.onSuccess {
                _consignmentManifest.value = this.data
            }.onError {
                map(HttpErrorMapper) {
                    when(this.code) {
                        Dock2DockErrorCode.Unauthorised -> {
                            _errorMessage.value = UNAUTHORISED_NETWORK_ERROR
                        }
                        Dock2DockErrorCode.NotFound -> {
                            _errorMessage.value = this.message
                        }
                        else -> {
                            _errorMessage.value = SERVER_NETWORK_ERROR
                        }
                    }
                }
            }.onException {
                _errorMessage.value = SERVER_NETWORK_ERROR
            }
            onIsLoadingChange(false)
        }
    }

    fun load() {
        getConsignmentManifest()
    }
}