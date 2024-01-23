package io.dock2dock.android.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skydoves.sandwich.map
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.onSuccess
import io.dock2dock.android.ApiService.getRetrofitClient
import io.dock2dock.android.SERVER_NETWORK_ERROR
import io.dock2dock.android.UNAUTHORISED_NETWORK_ERROR
import io.dock2dock.android.clients.PublicApiClient
import io.dock2dock.android.models.Dock2DockErrorCode
import io.dock2dock.android.models.HttpErrorMapper
import io.dock2dock.android.models.commands.DeleteCrossdockLabel
import io.dock2dock.android.models.query.CrossdockLabel
import io.dock2dock.android.models.query.CrossdockSalesOrder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

internal class CrossdockLabelDataTableViewModel(
    val salesOrderNo: String): ViewModel()
{
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading
    private fun onIsLoadingChange(value: Boolean) {
        _isLoading.value = value
    }

    private val _showErrorDialog = MutableLiveData(false)
    val showErrorDialog: LiveData<Boolean> = _showErrorDialog

    fun onCloseErrorDialog() {
        _showErrorDialog.value = false
    }

    private val _errorMessage = MutableLiveData("")
    val errorMessage: LiveData<String> = _errorMessage

    private var _items: MutableStateFlow<List<CrossdockLabel>> = MutableStateFlow(listOf())
    val items: StateFlow<List<CrossdockLabel>> get() = _items

    private val _salesOrder = MutableLiveData<CrossdockSalesOrder?>()
    val salesOrder: LiveData<CrossdockSalesOrder?> = _salesOrder

    private val _salesOrderNotFound = MutableLiveData<Boolean>(false)
    val salesOrderNotFound: LiveData<Boolean> = _salesOrderNotFound

    private val _selectedItem: MutableStateFlow<CrossdockLabel?> = MutableStateFlow(null)
    val selectedItem: StateFlow<CrossdockLabel?> get() = _selectedItem

    internal fun setSelectedItem(label: CrossdockLabel?) {
        _selectedItem.value = label
    }


    private val publicApiClient = getRetrofitClient<PublicApiClient>()

    init {
        getSalesOrder()
    }

    private fun getSalesOrder() {
        _errorMessage.value = ""
        viewModelScope.launch {
            onIsLoadingChange(true)
            val response = publicApiClient.getSalesOrder(salesOrderNo)
            response.onSuccess {
                _salesOrder.value = this.data
                _items.value = this.data.labels
            }.onError {
                map(HttpErrorMapper) {
                    when(this.code) {
                        Dock2DockErrorCode.Unauthorised -> {
                            _errorMessage.value = UNAUTHORISED_NETWORK_ERROR
                        }
                        Dock2DockErrorCode.NotFound -> {
                            _errorMessage.value = this.message
                            _salesOrderNotFound.value = true
                        }
                        else -> {
                            _errorMessage.value = SERVER_NETWORK_ERROR
                        }
                    }
                }
            }.onException {
                print("Error getting sales order. ${this.message}")
                _errorMessage.value = SERVER_NETWORK_ERROR
            }
            onIsLoadingChange(false)
            _showErrorDialog.value = errorMessage.value?.isNotEmpty()
        }
    }

    fun getCrossdockLabels() {
        _errorMessage.value = ""
        viewModelScope.launch {
            onIsLoadingChange(true)
            val response = publicApiClient.getLabels("salesOrderNo eq '$salesOrderNo'", "DateCreated desc")
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
            _showErrorDialog.value = errorMessage.value?.isNotEmpty()
        }
    }

    suspend fun deleteCrossdockLabel(label: CrossdockLabel) {
        _errorMessage.value = ""
        val cmd = DeleteCrossdockLabel(label.id, !label.isDeleted)
        val response = publicApiClient.deleteCrossdockLabel(cmd)
        response.onSuccess {
            onUpdateCrossdockLabel(cmd.isDeleted, label)
        }.onError {
            map(HttpErrorMapper) {
                when(this.code) {
                    Dock2DockErrorCode.Unauthorised -> _errorMessage.value = UNAUTHORISED_NETWORK_ERROR
                    Dock2DockErrorCode.BadRequest,
                    Dock2DockErrorCode.UnprocessableEntity,
                    Dock2DockErrorCode.NotFound,
                    Dock2DockErrorCode.Validation -> _errorMessage.value = this.message
                    else -> {
                        _errorMessage.value = SERVER_NETWORK_ERROR
                    }
                }
            }
        }.onException {
            _errorMessage.value = SERVER_NETWORK_ERROR
        }
        _showErrorDialog.value = errorMessage.value?.isNotEmpty()
    }

    private fun onUpdateCrossdockLabel(state: Boolean, item: CrossdockLabel) {
        _items.value = _items.value.map {
            if (it.id == item.id) {
                it.copy(isDeleted = state)
            } else {
                it
            }
        }
    }
}