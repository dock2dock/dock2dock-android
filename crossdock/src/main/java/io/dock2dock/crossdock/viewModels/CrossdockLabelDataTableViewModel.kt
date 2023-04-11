package io.dock2dock.crossdock.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.dock2dock.application.models.commands.DeleteCrossdockLabel
import io.dock2dock.application.models.query.*
import io.dock2dock.networking.ApiService.getRetrofitClient
import io.dock2dock.networking.clients.*
import io.dock2dock.networking.configuration.Dock2DockConfiguration
import io.dock2dock.networking.managers.TokenManager
import io.dock2dock.networking.models.Dock2DockErrorCode
import io.dock2dock.networking.models.HttpErrorMapper
import io.dock2dock.networking.utilities.Constants.PUBLICAPI_BASEURL
import kotlinx.coroutines.launch
import com.skydoves.sandwich.*

internal class CrossdockLabelDataTableViewModel(
    val tokenManager: TokenManager,
    val dock2DockConfiguration: Dock2DockConfiguration,
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

    private val publicApiClient = getRetrofitClient<PublicApiClient>(dock2DockConfiguration, PUBLICAPI_BASEURL)

    init {
        getCrossdockLabels()
    }

    fun getCrossdockLabels() {
        viewModelScope.launch {
            onIsLoadingChange(true)
            var response = publicApiClient.getLabels("salesOrderNo eq '$salesOrderNo'", "DateCreated desc")
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
            var cmd = DeleteCrossdockLabel(label.id, !label.isDeleted)
            var response = publicApiClient.deleteCrossdockLabel(cmd)
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
        var tempItems = items.value?.toMutableList()

        tempItems?.indexOfFirst { it.id == label.id }?.let {
            tempItems?.set(it, label)
            onItemsChange(tempItems)
        }
    }
}