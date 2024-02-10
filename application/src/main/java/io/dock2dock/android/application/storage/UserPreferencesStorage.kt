package io.dock2dock.android.application.storage

import android.content.Context
import android.content.SharedPreferences

internal class UserPreferencesStorage(context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun get(key: String): String? = prefs.run {
        return getString(key, "") ?: ""
    }

    fun getBoolean(key: String): Boolean = prefs.run {
        return getBoolean(key, false)
    }

    fun clear() = prefs.edit().clear().apply()

    fun update(key: String, value: String) {
        prefs.edit().apply {
            putString(key, value)
        }.apply()
    }

    fun update(key: String, value: Boolean) {
        prefs.edit().apply {
            putBoolean(key, value)
        }.apply()
    }

    companion object {
        private const val PREFS_NAME = "dock2dock"
    }
}