package io.dock2dock.android.freightmanagement.viewModels

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
import io.dock2dock.android.application.models.commands.CreateShippingContainerRequest
import io.dock2dock.android.application.models.query.ConsignmentProduct
import io.dock2dock.android.application.models.responses.CreateShippingContainerResponse
import io.dock2dock.android.networking.ApiService
import io.dock2dock.android.networking.SERVER_NETWORK_ERROR
import io.dock2dock.android.networking.UNAUTHORISED_NETWORK_ERROR
import io.dock2dock.android.networking.clients.PublicApiClient
import io.dock2dock.android.networking.models.Dock2DockErrorCode
import io.dock2dock.android.networking.models.HttpErrorMapper
import kotlinx.coroutines.launch

internal class CreateShippingContainerDialogViewModel(val onSuccess: (CreateShippingContainerResponse) -> Unit): ViewModel() {

    private val publicApiClient = ApiService.getRetrofitClient<PublicApiClient>()

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _loadError = MutableLiveData("")
    val loadError: LiveData<String> = _loadError
    private fun onLoadErrorChange(value: String) {
        _loadError.value = value
    }

    private var consignmentProduct by mutableStateOf<ConsignmentProduct?>(null)
    var consignmentProductName by mutableStateOf("")

    var consignmentProducts by mutableStateOf(listOf<ConsignmentProduct>())

    var consignmentProductIsError by mutableStateOf(false)
        private set

    //validation message
    val consignmentProductErrorMessage = "Consignment Product is a required field"

    fun onConsignmentProductValueChanged(value: ConsignmentProduct) {
        consignmentProduct = value
        consignmentProductName = value.name
        validateConsignmentProduct()
    }

    fun load() {
        getConsignmentProducts()
    }

    private fun getConsignmentProducts() {
        viewModelScope.launch {
            val response = publicApiClient.getConsignmentProducts()
            response.onSuccess {
                consignmentProducts = this.data.value
            }.onError {
                map(HttpErrorMapper) {
                    when(this.code) {
                        Dock2DockErrorCode.Unauthorised -> onLoadErrorChange(
                            UNAUTHORISED_NETWORK_ERROR
                        )
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

    private fun validateConsignmentProduct() {
        consignmentProductIsError =  consignmentProduct?.id.isNullOrEmpty()
    }

    private fun validateForm(): Boolean {
        validateConsignmentProduct()

        return !consignmentProductIsError
    }

    fun onSubmit() {
        if (!validateForm()) {
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            val command = CreateShippingContainerRequest(consignmentProduct?.id ?: "")
            val response = publicApiClient.createShippingContainer(command)
            response.onSuccess {
                onLoadErrorChange("")
                onSuccess(this.data)
            }.onError {
                map(HttpErrorMapper) {
                    when(this.code) {
                        Dock2DockErrorCode.Unauthorised -> onLoadErrorChange(
                            UNAUTHORISED_NETWORK_ERROR
                        )
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
                onLoadErrorChange(SERVER_NETWORK_ERROR)
            }

            _isLoading.value = false
        }
    }
}