package io.dock2dock.networking.configuration

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.RestrictTo

class Dock2DockConfiguration constructor(val apiKey: String) {

    private class Store(context: Context) {
        private val prefs: SharedPreferences = context.applicationContext.getSharedPreferences(NAME, 0)

        @JvmSynthetic
        fun save(
            apiKey: String
        ) {
            prefs.edit()
                .putString(KEY_API_KEY, apiKey)
                .apply()
        }

        fun load(): Dock2DockConfiguration? {
            val apiKey = prefs.getString(KEY_API_KEY, null)

            if (apiKey.isNullOrEmpty()) {
                return null
            }
            return Dock2DockConfiguration(apiKey = apiKey)
        }

        private companion object {
            private val NAME = Dock2DockConfiguration::class.java.canonicalName

            private const val KEY_API_KEY = "key_api_key"
        }
    }
    companion object {
        private var instance: Dock2DockConfiguration? = null

        fun getInstance(context: Context): Dock2DockConfiguration {
            return instance ?: loadInstance(context)
        }

        private fun loadInstance(context: Context): Dock2DockConfiguration {
            return Store(context).load()?.let {
                instance = it
                it
            }
                ?: throw IllegalStateException(
                    "Dock2DockConfiguration was not initialized. Call Dock2DockConfiguration.init()."
                )
        }

        fun init(context: Context,
                  apiKey: String) {
            instance = Dock2DockConfiguration(apiKey)

            Store(context)
                .save(apiKey)
        }

        @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
        fun clearInstance() {
            instance = null
        }


    }
}