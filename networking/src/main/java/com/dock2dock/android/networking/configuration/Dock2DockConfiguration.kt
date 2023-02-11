package com.dock2dock.android.networking.configuration

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.RestrictTo

class Dock2DockConfiguration constructor(val clientId: String,
                                                 val clientSecretKey: String) {

    private class Store(context: Context) {
        private val prefs: SharedPreferences = context.applicationContext.getSharedPreferences(NAME, 0)

        @JvmSynthetic
        fun save(
            clientId: String,
            clientSecretKey: String
        ) {
            prefs.edit()
                .putString(KEY_CLIENT_ID, clientId)
                .putString(KEY_CLIENT_SECRET_KEY, clientSecretKey)
                .apply()
        }

        internal fun load(): Dock2DockConfiguration? {
            var clientId = prefs.getString(KEY_CLIENT_ID, null)
            var clientSecretKey = prefs.getString(KEY_CLIENT_SECRET_KEY, null)

            if (clientId.isNullOrEmpty() || clientSecretKey.isNullOrEmpty()) {
                return null
            }
            return Dock2DockConfiguration(
                    clientId = clientId,
                    clientSecretKey = clientSecretKey
            )
        }

        private companion object {
            private val NAME = Dock2DockConfiguration::class.java.canonicalName

            private const val KEY_CLIENT_ID = "key_client_id"
            private const val KEY_CLIENT_SECRET_KEY = "key_client_secret_key"
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
                  clientId: String,
                  clientSecretKey: String) {
            instance = Dock2DockConfiguration(clientId, clientSecretKey)

            Store(context)
                .save(clientId, clientSecretKey)

        }

        @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
        fun clearInstance() {
            instance = null
        }


    }
}