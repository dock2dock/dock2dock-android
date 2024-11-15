package io.dock2dock.android.freightmanagement.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import io.dock2dock.android.application.configuration.Dock2DockConfiguration
import io.dock2dock.android.application.models.commands.CreateShippingContainerRequest
import io.dock2dock.android.application.models.commands.PrintConsignmentItemShippingLabelsRequest
import io.dock2dock.android.application.models.query.ConsignmentHeaderItem
import io.dock2dock.android.application.models.query.ConsignmentProduct
import io.dock2dock.android.application.models.query.ShippingContainer
import io.dock2dock.android.application.models.responses.CreateShippingContainerResponse
import io.dock2dock.android.networking.ApiService
import io.dock2dock.android.networking.SERVER_NETWORK_ERROR
import io.dock2dock.android.networking.UNAUTHORISED_NETWORK_ERROR
import io.dock2dock.android.networking.clients.PublicApiClient
import io.dock2dock.android.networking.models.Dock2DockErrorCode
import io.dock2dock.android.networking.models.HttpErrorMapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class PrintConsignmentItemViewModel(
    private val consignmentHeaderNo: String,
    val onSuccess: () -> Unit
): ViewModel() {

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData("")
    val errorMessage: LiveData<String> = _errorMessage

    private var consignmentHeaderItem by mutableStateOf<ConsignmentHeaderItem?>(null)
    var consignmentHeaderItemName by mutableStateOf("")

    var quantity by mutableIntStateOf(0)
    var deliveryInstructions by mutableStateOf<String?>(null)

    private val dock2dockConfiguration = Dock2DockConfiguration.instance()
    private val publicApiClient = ApiService.getRetrofitClient<PublicApiClient>()

    var consignmentHeaderItems by mutableStateOf(listOf<ConsignmentHeaderItem>())

    var consignmentHeaderItemIsError by mutableStateOf(false)
        private set

    var quantityIsError by mutableStateOf(false)
        private set

    //validation message
    val consignmentHeaderItemErrorMessage = "Please select consignment item"
    val quantityErrorMessage = "Quantity is required"

    fun load() {
        getConsignmentHeaderItems()
    }

    fun onConsignmentHeaderItemValueChanged(value: ConsignmentHeaderItem) {
        consignmentHeaderItem = value
        consignmentHeaderItemName = value.description
        quantity = if (value.isPallet) { value.pallets } else { value.quantity.toInt() }
        validateConsignmentHeaderItem()
    }

    fun onQuantityValueChanged(value: Int) {
        quantity = value
        validateQuantity()
    }

    fun onDeliveryInstructionsValueChanged(value: String?) {
        deliveryInstructions = value
    }

    private fun getConsignmentHeaderItems() {
        viewModelScope.launch {
            val response =
                publicApiClient.getConsignmentHeaderItems("consignmentHeaderNo eq '${consignmentHeaderNo}'")
            response.onSuccess {
                consignmentHeaderItems = this.data.value
            }.onError {
                map(HttpErrorMapper) {
                    when (this.code) {
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

    private fun validateConsignmentHeaderItem() {
        consignmentHeaderItemIsError = consignmentHeaderItem == null
    }
    private fun validateQuantity() {
        quantityIsError = quantity <= 0
    }

    private fun validateForm(): Boolean {
        validateConsignmentHeaderItem()

        return !consignmentHeaderItemIsError && !quantityIsError;
    }

    fun onSubmit() {
        if (!validateForm()) {
            return
        }
        val defaultPrinterId = dock2dockConfiguration.getDefaultPrinter().let { printer ->
            if (printer.isNullOrEmpty()) {
                _errorMessage.value = "Please select default printer under Dock2Dock settings ⚙"
                return
            } else {
                printer
            }
        }

        viewModelScope.launch {
            _isLoading.value = true
            val command = PrintConsignmentItemShippingLabelsRequest(consignmentHeaderItem?.id ?: "", defaultPrinterId, quantity, deliveryInstructions)
            val response = publicApiClient.consignmentHeaderItemPrintShippingLabels(command)
            response.onSuccess {
                onLoadErrorChange("")
                onSuccess()
            }.onError {
                map(HttpErrorMapper) {
                    when (this.code) {
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

    private fun onLoadErrorChange(value: String) {
        _errorMessage.value = value
    }
}