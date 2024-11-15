package io.dock2dock.android.freightmanagement.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import io.dock2dock.android.application.models.query.ShippingContainer
import io.dock2dock.android.networking.ApiService
import io.dock2dock.android.networking.SERVER_NETWORK_ERROR
import io.dock2dock.android.networking.UNAUTHORISED_NETWORK_ERROR
import io.dock2dock.android.networking.clients.PublicApiClient
import io.dock2dock.android.networking.models.Dock2DockErrorCode
import io.dock2dock.android.networking.models.HttpErrorMapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

internal class ConsignmentManifestsScreenViewModel: ViewModel() {
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading
    private fun onIsLoadingChange(value: Boolean) {
        _isLoading.value = value
    }

    fun onCloseErrorDialog() {
        _errorMessage.value = ""
    }

    private val _errorMessage = MutableLiveData("")
    val errorMessage: LiveData<String> = _errorMessage

    private var _items: MutableStateFlow<List<FreightConsignmentManifest>> = MutableStateFlow(listOf())
    val items: StateFlow<List<FreightConsignmentManifest>> get() = _items

    private val _selectedItem: MutableStateFlow<FreightConsignmentManifest?> = MutableStateFlow(null)
    val selectedItem: StateFlow<FreightConsignmentManifest?> get() = _selectedItem

    var showPickupLocation: Boolean = false

    internal fun setSelectedItem(label: FreightConsignmentManifest?) {
        _selectedItem.value = label
    }

    private val dock2dockConfiguration = Dock2DockConfiguration.instance()
    private val publicApiClient = ApiService.getRetrofitClient<PublicApiClient>()

    init {
        getManifests()
        showPickupLocation = dock2dockConfiguration.getDefaultPickupAddress()?.isEmpty() ?: true
    }

    fun refresh() {
        getManifests()
    }

    private fun getManifests() {
        _errorMessage.value = ""

        val pickupAddressId = dock2dockConfiguration.getDefaultPickupAddress()

        var filter: String? = null
        if (pickupAddressId?.isNotEmpty() == true) {
            filter = "pickupAddressId eq '$pickupAddressId'"
        }

        viewModelScope.launch {
            onIsLoadingChange(true)
            val response = publicApiClient.getConsignmentManifests(filter)
            response.onSuccess {
                _items.value = this.data.value
            }.onError {
                map(HttpErrorMapper) {
                    when(this.code) {
                        Dock2DockErrorCode.Unauthorised -> {
                            _errorMessage.value = UNAUTHORISED_NETWORK_ERROR
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
}

