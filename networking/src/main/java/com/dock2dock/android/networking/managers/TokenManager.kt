package com.dock2dock.android.networking.managers

import android.content.Context
import android.content.SharedPreferences
import java.util.*

class TokenManager constructor(context: Context) {
    private var prefs: SharedPreferences = context.getSharedPreferences("Dock2Dock", Context.MODE_PRIVATE)

    companion object {
        private var instance : TokenManager? = null

        fun getInstance(context: Context): TokenManager {
            if (instance == null)
                instance = TokenManager(context)

            return instance!!
        }

        private val USER_TOKEN = "user_token"
        private val EXPIRY_DATE = "expiry_date"
    }

    /**
     * Function to save auth token
     */
    fun saveToken(token: String, expiryDate: Long) {
        val editor = prefs.edit()
        editor.putString(USER_TOKEN, token)
        editor.putLong(EXPIRY_DATE, expiryDate)
        editor.apply()
    }

    /**
     * Function to fetch auth token
     */
    fun getToken(): String? {
        var expiryDate = prefs.getLong(EXPIRY_DATE, 0)

        var calendar = Calendar.getInstance()
        calendar.add(Calendar.SECOND, 30) //

        var diff = expiryDate - calendar.timeInMillis
        println("Expiry time diff = $diff, now = ${calendar.timeInMillis}, expiryDate = $expiryDate")// Now + 30 seconds

        if (expiryDate < calendar.timeInMillis) {
            deleteToken()
            return null
        }

        return prefs.getString(USER_TOKEN, null)
    }

    private fun deleteToken() {
        prefs.edit().remove(USER_TOKEN)
    }


}