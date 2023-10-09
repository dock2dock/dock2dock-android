package io.dock2dock.android.storage

import android.content.Context
import android.content.SharedPreferences

internal class UserPreferencesStorage(context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun get(key: String): String? = prefs.run {
        return getString(key, "") ?: ""
    }

    fun clear() = prefs.edit().clear().apply()

    fun update(key: String, value: String) {
        prefs.edit().apply {
            putString(key, value)
        }.apply()
    }

    companion object {
        private const val PREFS_NAME = "dock2dock"
    }
}