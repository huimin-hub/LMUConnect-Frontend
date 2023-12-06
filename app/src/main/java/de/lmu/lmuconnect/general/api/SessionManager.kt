package de.lmu.lmuconnect.general.api

import android.content.Context
import android.content.SharedPreferences
import de.lmu.lmuconnect.R

class SessionManager(context: Context) {
    private var prefs: SharedPreferences = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)

    companion object {
        const val USER_TOKEN = "user_token"
    }

    /**
     * Function to save auth token
     */
    fun saveAuthToken(token: String) {
        val editor = prefs.edit()
        editor.putString(USER_TOKEN, token)
        editor.apply()
    }

    /**
     * Function to fetch auth token
     */
    fun fetchAuthToken(): String {
        return prefs.getString(USER_TOKEN, null).orEmpty()
    }

    /**
     * Function to check if auth token exists
     */
    fun authTokenIsEmpty(): Boolean {
        return prefs.getString(USER_TOKEN, null).isNullOrEmpty()
    }
}