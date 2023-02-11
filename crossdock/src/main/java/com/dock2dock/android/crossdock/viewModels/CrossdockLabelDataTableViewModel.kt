package com.dock2dock.android.crossdock.viewModels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dock2dock.android.application.models.commands.DeleteCrossdockLabel
import com.dock2dock.android.application.models.query.*
import com.dock2dock.android.networking.ApiService.getRetrofitClient
import com.dock2dock.android.networking.clients.*
import com.dock2dock.android.networking.configuration.Dock2DockConfiguration
import com.dock2dock.android.networking.managers.TokenManager
import com.dock2dock.android.networking.utilities.Constants.PUBLICAPI_BASEURL
import kotlinx.coroutines.launch
import com.skydoves.sandwich.*

internal class CrossdockLabelDataTableViewModel(
    tokenManager: TokenManager,
    dock2DockConfiguration: Dock2DockConfiguration,
    private val salesOrderId: String
    ): ViewModel()
{
    var isLoading = mutableStateOf(false)
    var loadError = mutableStateOf("")
    var items = mutableStateOf(listOf<CrossdockLabel>())

    private val publicApiClient = getRetrofitClient<PublicApiClient>(tokenManager, dock2DockConfiguration, PUBLICAPI_BASEURL)

    init {
        getCrossdockLabels()
    }

    fun getCrossdockLabels() {
        viewModelScope.launch {
            isLoading.value = true
            var response = publicApiClient.getLabels("salesOrderNo eq '$salesOrderId'")
            response.onSuccess {
                loadError.value = ""
                items.value = this.data.value
            }.onError {
                when(this.statusCode) {
                    StatusCode.Unauthorized -> loadError.value = "We couldn't validate your credentials. Please check before continuing."
                    else -> {
                        loadError.value = "An error has occurred. Please retry or contact Dock2Dock support team."
                    }
                }
//                map(HttpErrorMapper) {
//                    val code = this.code
//                    val message = this.message
//                }
            }.onException {
                loadError.value = "An error has occurred. Please retry or contact Dock2Dock support team."
            }

            isLoading.value = false
        }
    }

    fun deleteCrossdockLabel(label: CrossdockLabel) {
        viewModelScope.launch {
            var label = DeleteCrossdockLabel(label.id, !label.isDeleted)
            var response = publicApiClient.deleteCrossdockLabel(label)
            response.onSuccess {

            }.onError {

            }.onException {

            }

            isLoading.value = false
        }
    }
}