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
import io.dock2dock.android.clients.PublicApiClient
import io.dock2dock.android.models.Dock2DockErrorCode
import io.dock2dock.android.models.HttpErrorMapper
import io.dock2dock.android.models.commands.DeleteCrossdockLabel
import io.dock2dock.android.models.query.CrossdockLabel
import kotlinx.coroutines.launch

internal class CrossdockLabelDataTableViewModel(
    val salesOrderNo: String): ViewModel()
{
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading
    private fun onIsLoadingChange(value: Boolean) {
        _isLoading.value = value
    }

    private val _loadError = MutableLiveData("")
    val loadError: LiveData<String> = _loadError
    private fun onLoadErrorChange(value: String) {
        _loadError.value = value
    }

    private val _items = MutableLiveData(listOf<CrossdockLabel>())
    var items: LiveData<List<CrossdockLabel>> = _items
    private fun onItemsChange(value: List<CrossdockLabel>) {
        _items.value = value
    }

    private val publicApiClient = getRetrofitClient<PublicApiClient>()

    init {
        getCrossdockLabels()
    }

    fun getCrossdockLabels() {
        viewModelScope.launch {
            onIsLoadingChange(true)
            val response = publicApiClient.getLabels("salesOrderNo eq '$salesOrderNo'", "DateCreated desc")
            response.onSuccess {
                onLoadErrorChange("")
                onItemsChange(this.data.value)
            }.onError {
                map(HttpErrorMapper) {
                    when(this.code) {
                        Dock2DockErrorCode.Unauthorised -> onLoadErrorChange("We couldn't validate your credentials. Please check before continuing.")
                        else -> {
                            onLoadErrorChange("An error has occurred. Please retry or contact Dock2Dock support team.")
                        }
                    }
                }
            }.onException {
                onLoadErrorChange("An error has occurred. Please retry or contact Dock2Dock support team.")
            }

            onIsLoadingChange(false)
        }
    }

    fun deleteCrossdockLabel(label: CrossdockLabel) {
        viewModelScope.launch {
            val cmd = DeleteCrossdockLabel(label.id, !label.isDeleted)
            val response = publicApiClient.deleteCrossdockLabel(cmd)
            response.onSuccess {

                label.isDeleted = cmd.isDeleted
                onUpdateCrossdockLabel(label)
            }.onError {
                map(HttpErrorMapper) {
                    when(this.code) {
                        Dock2DockErrorCode.Unauthorised -> onLoadErrorChange("We couldn't validate your credentials. Please check before continuing.")
                        else -> {
                            onLoadErrorChange("An error has occurred. Please retry or contact Dock2Dock support team.")
                        }
                    }
                }
            }.onException {

            }
        }
    }

    private fun onUpdateCrossdockLabel(label: CrossdockLabel) {
        val tempItems = items.value?.toMutableList()

        tempItems?.indexOfFirst { it.id == label.id }?.let {
            tempItems?.set(it, label)
            onItemsChange(tempItems)
        }
    }
}