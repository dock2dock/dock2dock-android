package io.dock2dock.android.crossdock.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skydoves.sandwich.map
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.onSuccess
import io.dock2dock.android.application.configuration.Dock2DockConfiguration
import io.dock2dock.android.application.eventBus.Dock2DockEventBus
import io.dock2dock.android.application.events.Dock2DockDefaultHandlingUnitChangedEvent
import io.dock2dock.android.application.events.Dock2DockSalesOrderRetrievedEvent
import io.dock2dock.android.application.events.LicensePlateSetToActiveEvent
import io.dock2dock.android.application.models.commands.CreateLicensePlateRequest
import io.dock2dock.android.application.models.query.CrossdockHandlingUnit
import io.dock2dock.android.networking.ApiService
import io.dock2dock.android.networking.clients.PublicApiClient
import io.dock2dock.android.networking.models.HttpErrorMapper
import kotlinx.coroutines.launch

class LicensePlateSettingsViewModel(val salesOrderNo: String): ViewModel() {

    private val publicApiClient = ApiService.getRetrofitClient<PublicApiClient>()
    var handlingUnits by mutableStateOf(listOf<CrossdockHandlingUnit>())
    private val dock2dockConfiguration = Dock2DockConfiguration.instance()

    var selectedHandlingUnitText by mutableStateOf("")
    private var selectedHandlingUnitId: String? by mutableStateOf(null)

    var printCrossdockLabel by mutableStateOf(false)

    var showPrintCrossdockLabel by mutableStateOf(true)

    init {
        subscribeToSalesOrderRetrievedEvent()
        subscribeToHandlingUnitChangedEvent()
        getHandlingUnits()
        printCrossdockLabel = dock2dockConfiguration.getPrintCrossdockLabelSetting()
    }

    fun onPrintCrossdockLabelChanged(value: Boolean) {
        printCrossdockLabel = value
        dock2dockConfiguration.updatePrintCrossdockLabelSetting(value)
    }

    fun onSelectedHandlingUnitChanged(value: CrossdockHandlingUnit) {
        selectedHandlingUnitText = value.name
        selectedHandlingUnitId = value.id
        dock2dockConfiguration.updateDefaultHandlingUnit(value.id)
    }

    private fun getHandlingUnits() {
        viewModelScope.launch {
            val response = publicApiClient.getCrossdockHandlingUnits()
            response.onSuccess {
                handlingUnits = this.data.value

                val handlingUnitId = dock2dockConfiguration.getDefaultHandlingUnit()
                if (!handlingUnitId.isNullOrEmpty()) {
                    val handlingUnit = handlingUnits.firstOrNull { it.id == handlingUnitId }
                    selectedHandlingUnitText = handlingUnit?.name ?: ""
                    selectedHandlingUnitId = handlingUnitId
                }
            }.onError {
                map(HttpErrorMapper) {
                    when(this.code) {
//                        Dock2DockErrorCode.Unauthorised -> onLoadErrorChange(
//                            UNAUTHORISED_NETWORK_ERROR
//                        )
//                        else -> {
//                            onLoadErrorChange(SERVER_NETWORK_ERROR)
//                        }
                    }
                }
            }.onException {
                //onLoadErrorChange("An error has occurred. Please retry or contact Dock2Dock support team.")
            }
        }
    }

    fun onAdd() {
        viewModelScope.launch {
            //_isLoading.value = true
            val request = CreateLicensePlateRequest(salesOrderNo, selectedHandlingUnitId ?: "", printCrossdockLabel)
            val response = publicApiClient.createLicensePlate(request)
            response.onSuccess {
                val activeEvent = LicensePlateSetToActiveEvent(this.data.licensePlateNo)

                viewModelScope.launch {
                    Dock2DockEventBus.publish(activeEvent)
                }
            }.onError {
                map(HttpErrorMapper) {
                    when(this.code) {
//                        Dock2DockErrorCode.Unauthorised -> onLoadErrorChange(
//                            UNAUTHORISED_NETWORK_ERROR
//                        )
//                        Dock2DockErrorCode.BadRequest,
//                        Dock2DockErrorCode.UnprocessableEntity,
//                        Dock2DockErrorCode.NotFound,
//                        Dock2DockErrorCode.Validation -> onLoadErrorChange(this.message)
//                        else -> {
//                            onLoadErrorChange(SERVER_NETWORK_ERROR)
//                        }
                    }
                }
            }.onException {
                //onLoadErrorChange("An error has occurred. Please retry or contact Dock2Dock support team.")
            }
            //_isLoading.value = false
        }
    }

    private fun subscribeToSalesOrderRetrievedEvent() {
        viewModelScope.launch {
            Dock2DockEventBus.subscribe<Dock2DockSalesOrderRetrievedEvent> {
                showPrintCrossdockLabel = it.isCrossdock
            }
        }
    }

    private fun subscribeToHandlingUnitChangedEvent() {
        viewModelScope.launch {
            Dock2DockEventBus.subscribe<Dock2DockDefaultHandlingUnitChangedEvent> {
                selectedHandlingUnitText = it.handlingUnit.name
                selectedHandlingUnitId = it.handlingUnit.id
            }
        }
    }
}