package io.dock2dock.android.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.map
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.onSuccess
import io.dock2dock.android.ApiService
import io.dock2dock.android.clients.PublicApiClient
import io.dock2dock.android.configuration.Dock2DockConfiguration
import io.dock2dock.android.models.HttpErrorMapper
import io.dock2dock.android.models.ODataResponse
import io.dock2dock.android.models.query.CrossdockHandlingUnit
import io.dock2dock.android.models.query.Printer
import kotlinx.coroutines.launch

internal class SettingsDialogViewModel: ViewModel() {
    private val publicApiClient = ApiService.getRetrofitClient<PublicApiClient>()

    var handlingUnits by mutableStateOf(listOf<CrossdockHandlingUnit>())
    var printers by mutableStateOf(listOf<Printer>())

    var selectedHandlingUnitText by mutableStateOf("")

    var selectedPrinterText by mutableStateOf("")

    private val dock2dockConfiguration = Dock2DockConfiguration.instance()

    fun load() {
        getHandlingUnits()
        getPrinters()
    }

    fun onHandlingUnitValueChanged(value: CrossdockHandlingUnit) {
        dock2dockConfiguration.updateDefaultHandlingUnit(value.id)
        selectedHandlingUnitText = value.name
    }

    fun onPrinterValueChanged(value: Printer) {
        dock2dockConfiguration.updatePrinter(value.id)
        selectedPrinterText = value.name
    }

    private fun getHandlingUnits() {
        viewModelScope.launch {
            val response = publicApiClient.getCrossdockHandlingUnits()
            response.onSuccess {
                handlingUnits = mutableListOf<CrossdockHandlingUnit>().apply {
                    add(CrossdockHandlingUnit("", "--None selected--"))
                    addAll(data.value)
                }

                val handlingUnitId = dock2dockConfiguration.getDefaultHandlingUnit()
                if (!handlingUnitId.isNullOrEmpty()) {
                    selectedHandlingUnitText = handlingUnits.firstOrNull { it.id == handlingUnitId }?.name ?: ""
                }
            }.onError {
                map(HttpErrorMapper) {
                    when(this.code) {
//                        Dock2DockErrorCode.Unauthorised -> onLoadErrorChange("We couldn't validate your credentials. Please check before continuing.")
//                        else -> {
//                            onLoadErrorChange("An error has occurred. Please retry or contact Dock2Dock support team.")
//                        }
                    }
                }
            }.onException {
//                onLoadErrorChange("An error has occurred. Please retry or contact Dock2Dock support team.")
            }
        }
    }

    private fun getPrinters() {
        viewModelScope.launch {
            val response: ApiResponse<ODataResponse<Printer>> = publicApiClient.getPrinters("Name asc")
            response.onSuccess {
                printers = mutableListOf<Printer>().apply {
                    add(Printer("", "--None selected--"))
                    addAll(data.value)
                }

                val printerId = dock2dockConfiguration.getDefaultPrinter()
                selectedPrinterText = printers.firstOrNull { it.id == printerId }?.name ?: printers.first().name
            }.onError {
                map(HttpErrorMapper) {
                    when(this.code) {
//                        Dock2DockErrorCode.Unauthorised -> onLoadErrorChange("We couldn't validate your credentials. Please check before continuing.")
//                        else -> {
//                            onLoadErrorChange("An error has occurred. Please retry or contact Dock2Dock support team.")
//                        }
                    }
                }
            }.onException {
//                onLoadErrorChange("An error has occurred. Please retry or contact Dock2Dock support team.")
            }
        }
    }
}