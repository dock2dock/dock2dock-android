package io.dock2dock.android.configuration

import android.content.Context
import androidx.annotation.RestrictTo
import io.dock2dock.android.storage.*

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

    fun getDefaultPrinter(): String? {
        return userPreferences.get(DEFAULT_PRINTER)
    }

    companion object {
        private var instance: Dock2DockConfiguration? = null

        private const val DEFAULT_HANDLING_UNIT = "default_handling_unit"
        private const val DEFAULT_PRINTER = "default_printer"
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

