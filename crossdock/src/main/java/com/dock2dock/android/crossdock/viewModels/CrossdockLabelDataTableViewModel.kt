package com.dock2dock.android.crossdock.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dock2dock.android.application.models.commands.DeleteCrossdockLabel
import com.dock2dock.android.application.models.query.*
import com.dock2dock.android.networking.ApiService.getRetrofitClient
import com.dock2dock.android.networking.clients.*
import com.dock2dock.android.networking.configuration.Dock2DockConfiguration
import com.dock2dock.android.networking.managers.TokenManager
import com.dock2dock.android.networking.models.Dock2DockErrorCode
import com.dock2dock.android.networking.models.HttpErrorMapper
import com.dock2dock.android.networking.utilities.Constants.PUBLICAPI_BASEURL
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