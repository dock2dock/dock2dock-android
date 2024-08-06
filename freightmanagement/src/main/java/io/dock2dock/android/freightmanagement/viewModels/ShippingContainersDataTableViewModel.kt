package io.dock2dock.android.freightmanagement.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skydoves.sandwich.map
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.onSuccess
import io.dock2dock.android.application.models.query.ShippingContainer
import io.dock2dock.android.networking.ApiService.getRetrofitClient
import io.dock2dock.android.networking.SERVER_NETWORK_ERROR
import io.dock2dock.android.networking.UNAUTHORISED_NETWORK_ERROR
import io.dock2dock.android.networking.clients.PublicApiClient
import io.dock2dock.android.networking.models.Dock2DockErrorCode
import io.dock2dock.android.networking.models.HttpErrorMapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

internal class ShippingContainersDataTableViewModel: ViewModel()
{
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

    private var _items: MutableStateFlow<List<ShippingContainer>> = MutableStateFlow(listOf())
    val items: StateFlow<List<ShippingContainer>> get() = _items

    private val _selectedItem: MutableStateFlow<ShippingContainer?> = MutableStateFlow(null)
    val selectedItem: StateFlow<ShippingContainer?> get() = _selectedItem

    internal fun setSelectedItem(label: ShippingContainer?) {
        _selectedItem.value = label
    }

    private val publicApiClient = getRetrofitClient<PublicApiClient>()

    init {
        getShippingContainers()
    }

    fun refresh() {
        getShippingContainers()
    }

    private fun getShippingContainers() {
        _errorMessage.value = ""
        viewModelScope.launch {
            onIsLoadingChange(true)
            val response = publicApiClient.getShippingContainers()
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