package io.dock2dock.android.application.configuration

import android.content.Context
import androidx.annotation.RestrictTo
import io.dock2dock.android.application.storage.UserPreferencesStorage

data class Dock2DockConfiguration
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)

internal constructor(
    val apiKey: String,
    private val userPreferences: UserPreferencesStorage,
    val apiUrl: String) {

    fun updateDefaultHandlingUnit(value: String) {
        userPreferences.update(DEFAULT_HANDLING_UNIT, value)
    }

    fun getDefaultHandlingUnit(): String? {
        return userPreferences.get(DEFAULT_HANDLING_UNIT)
    }

    fun updatePrinter(value: String) {
        userPreferences.update(DEFAULT_PRINTER, value)
    }

    fun updatePickupAddress(value: String) {
        userPreferences.update(DEFAULT_PICKUP_ADDRESS, value)
    }

    fun getDefaultPrinter(): String? {
        return userPreferences.get(DEFAULT_PRINTER)
    }

    fun updatePrintCrossdockLabelSetting(value: Boolean) {
        userPreferences.update(DEFAULT_PRINT_CROSSDOCK_LBL_SETTING, value)
    }

    fun getPrintCrossdockLabelSetting(): Boolean {
        return userPreferences.getBoolean(DEFAULT_PRINT_CROSSDOCK_LBL_SETTING, true)
    }

    fun getDefaultPickupAddress(): String? {
        return userPreferences.get(DEFAULT_PICKUP_ADDRESS)
    }

    fun updateShowLPQuickCreateViewSetting(value: Boolean) {
        userPreferences.update(DEFAULT_SHOW_LP_QUICK_CREATE_VIEW_SETTING, value)
    }

    fun getShowLPQuickCreateViewSetting(): Boolean {
        return userPreferences.getBoolean(DEFAULT_SHOW_LP_QUICK_CREATE_VIEW_SETTING, true)
    }

    companion object {
        private var instance: Dock2DockConfiguration? = null

        private const val DEFAULT_HANDLING_UNIT = "default_handling_unit"
        private const val DEFAULT_PRINTER = "default_printer"
        private const val DEFAULT_PICKUP_ADDRESS = "default_pickupAddress"
        private const val DEFAULT_PRINT_CROSSDOCK_LBL_SETTING = "default_print_crossdock_label"
        private const val DEFAULT_SHOW_LP_QUICK_CREATE_VIEW_SETTING = "default_show_lp_quick_create_view_settings"
        private const val PUBLIC_API_BASEURL = "https://api.dock2dock.io/"

        @JvmStatic
        fun instance(): Dock2DockConfiguration {
            return instance
                ?: throw IllegalStateException(
                    "Dock2DockConfiguration was not initialized. Call Dock2DockConfiguration.init()."
                )
        }

        fun init(context: Context,
                  apiKey: String,
                 apiUrl: String? = null) {
            instance = Dock2DockConfiguration(
                apiKey = apiKey,
                userPreferences = UserPreferencesStorage(context),
                apiUrl = getUrl(apiUrl))
        }

        @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
        fun clearInstance() {
            instance = null
        }

        private fun getUrl(apiUrl: String? = null): String {
            return apiUrl ?: PUBLIC_API_BASEURL
        }
    }
}

