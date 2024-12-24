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
import io.dock2dock.android.application.models.commands.PrintManualConsignmentShippingLabelsRequest
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

    private var consignmentProduct by mutableStateOf<ConsignmentProduct?>(null)
    var consignmentProductName by mutableStateOf("")

    var quantity by mutableIntStateOf(0)
    var isManual by mutableStateOf(false)
    var deliveryInstructions by mutableStateOf<String?>(null)

    private val dock2dockConfiguration = Dock2DockConfiguration.instance()
    private val publicApiClient = ApiService.getRetrofitClient<PublicApiClient>()

    var consignmentHeaderItems by mutableStateOf(listOf<ConsignmentHeaderItem>())
    var consignmentProducts by mutableStateOf(listOf<ConsignmentProduct>())

    var consignmentHeaderItemIsError by mutableStateOf(false)
        private set

    var consignmentProductIsError by mutableStateOf(false)
        private set

    var quantityIsError by mutableStateOf(false)
        private set

    //validation message
    val consignmentHeaderItemErrorMessage = "Please select consignment item"
    val consignmentProductErrorMessage = "Please select consignment product"
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

    fun onConsignmentProductValueChanged(value: ConsignmentProduct) {
        consignmentProduct = value
        consignmentProductName = value.name
        validateConsignmentHeaderItem()
    }

    fun onQuantityValueChanged(value: Int) {
        quantity = value
        validateQuantity()
    }

    fun onDeliveryInstructionsValueChanged(value: String?) {
        deliveryInstructions = value
    }

    fun isManualValueChanged(value: Boolean) {
        if (!consignmentProductsLoaded) {
            getConsignmentProducts()
        }

        isManual = value

        if (isManual) {
            consignmentHeaderItem = null
            consignmentHeaderItemName = ""

        } else {
            consignmentProduct = null
            consignmentProductName = ""
        }
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

    private var consignmentProductsLoaded = false;
    private fun getConsignmentProducts() {
        viewModelScope.launch {
            val response =
                publicApiClient.getConsignmentProducts()
            response.onSuccess {
                consignmentProducts = this.data.value
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
            consignmentProductsLoaded = true
        }
    }

    private fun validateConsignmentHeaderItem() {
        consignmentHeaderItemIsError = consignmentHeaderItem == null
    }
    private fun validateConsignmentProduct() {
        consignmentProductIsError = consignmentProduct == null
    }
    private fun validateQuantity() {
        quantityIsError = quantity <= 0
    }

    private fun validateForm(): Boolean {
        val result = if (isManual) {
            validateConsignmentProduct()
            !consignmentProductIsError
        } else {
            validateConsignmentHeaderItem()
            !consignmentHeaderItemIsError
        }

        return result && !quantityIsError
    }

    fun onSubmit() {
        val defaultPrinterId = dock2dockConfiguration.getDefaultPrinter().let { printer ->
            if (printer.isNullOrEmpty()) {
                _errorMessage.value = "Please select default printer under Dock2Dock settings ⚙"
                return
            } else {
                printer
            }
        }

        if (!validateForm()) {
            return
        }

        if (isManual) {
            printManualShippingLabels(defaultPrinterId)
        } else {
            printConsignmentItemLabels(defaultPrinterId)
        }

    }

    private fun printConsignmentItemLabels(printerId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val command = PrintConsignmentItemShippingLabelsRequest(consignmentHeaderItem?.id ?: "", printerId, quantity, deliveryInstructions)
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

    private fun printManualShippingLabels(printerId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val command = PrintManualConsignmentShippingLabelsRequest(consignmentHeaderNo, consignmentProduct?.id ?: "", printerId, quantity, deliveryInstructions)
            val response = publicApiClient.consignmentHeaderPrintManualShippingLabels(command)
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