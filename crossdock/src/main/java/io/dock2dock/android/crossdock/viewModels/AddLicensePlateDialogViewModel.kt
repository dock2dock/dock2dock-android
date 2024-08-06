package io.dock2dock.android.crossdock.viewModels

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
import io.dock2dock.android.application.configuration.Dock2DockConfiguration
import io.dock2dock.android.application.eventBus.Dock2DockEventBus
import io.dock2dock.android.application.events.LicensePlateSetToActiveEvent
import io.dock2dock.android.application.models.commands.CreateLicensePlateRequest
import io.dock2dock.android.application.models.query.CrossdockHandlingUnit
import io.dock2dock.android.networking.SERVER_NETWORK_ERROR
import io.dock2dock.android.networking.UNAUTHORISED_NETWORK_ERROR
import io.dock2dock.android.networking.ApiService
import io.dock2dock.android.networking.clients.PublicApiClient
import io.dock2dock.android.networking.models.Dock2DockErrorCode
import io.dock2dock.android.networking.models.HttpErrorMapper
import kotlinx.coroutines.launch

internal class AddLicensePlateDialogViewModel(
    private val salesOrderNo: String,
    private val onSuccess: () -> Unit,
): ViewModel() {
    private val publicApiClient = ApiService.getRetrofitClient<PublicApiClient>()

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _loadError = MutableLiveData("")
    val loadError: LiveData<String> = _loadError
    private fun onLoadErrorChange(value: String) {
        _loadError.value = value
    }

    var handlingUnit by mutableStateOf<CrossdockHandlingUnit?>(null)
    var handlingUnitName by mutableStateOf<String>("")
    var handlingUnits by mutableStateOf(listOf<CrossdockHandlingUnit>())

    private val dock2dockConfiguration = Dock2DockConfiguration.instance()

    fun load() {
        getHandlingUnits()
    }

    var handlingUnitIdIsError by mutableStateOf(false)
        private set

    //validation message
    val handlingUnitIdErrorMessage = "Handling Unit is a required field"

    private fun validateHandlingUnitId() {
        handlingUnitIdIsError = handlingUnit?.id.isNullOrEmpty()
    }

    private fun validateForm(): Boolean {
        validateHandlingUnitId()

        return !handlingUnitIdIsError
    }

    fun onHandlingUnitValueChanged(value: CrossdockHandlingUnit) {
        handlingUnit = value
        handlingUnitName = value.name
        validateHandlingUnitId()
    }

    private fun getHandlingUnits() {
        viewModelScope.launch {
            val response = publicApiClient.getCrossdockHandlingUnits()
            response.onSuccess {
                handlingUnits = this.data.value

                var handlingUnitId = dock2dockConfiguration.getDefaultHandlingUnit()
                if (!handlingUnitId.isNullOrEmpty()) {
                    handlingUnit = handlingUnits.firstOrNull { it.id == handlingUnitId }
                    handlingUnitName = handlingUnit?.name ?: ""
                }
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
                onLoadErrorChange("An error has occurred. Please retry or contact Dock2Dock support team.")
            }
        }
    }

    fun onSubmit() {
        if (!validateForm()) {
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            val request = CreateLicensePlateRequest(salesOrderNo, handlingUnit?.id ?: "", true)
            val response = publicApiClient.createLicensePlate(request)
            response.onSuccess {
                val activeEvent = LicensePlateSetToActiveEvent(this.data.licensePlateNo)

                viewModelScope.launch {
                    Dock2DockEventBus.publish(activeEvent)
                }
                onSuccess()
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
                onLoadErrorChange("An error has occurred. Please retry or contact Dock2Dock support team.")
            }
            _isLoading.value = false
        }
    }
}