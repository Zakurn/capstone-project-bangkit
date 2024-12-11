package com.example.helotani.data.pref

import android.content.Context
import android.content.SharedPreferences
import com.example.helotani.data.HistoryItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class UserPref(context: Context) {
    private val pref: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "user_prefs"
        private const val IS_LOGGED_IN = "is_logged_in"
        private const val USER_EMAIL = "user_email"
        private const val USER_NAME = "user_name"
        private const val HISTORY_LIST = "history_list"
    }

    fun setLoginStatus(isLoggedIn: Boolean, email: String, name: String) {
        pref.edit().apply {
            putBoolean(IS_LOGGED_IN, isLoggedIn)
            putString(USER_EMAIL, email)
            putString(USER_NAME, name)
            apply()
        }
    }

    fun isLoggedIn(): Boolean = pref.getBoolean(IS_LOGGED_IN, false)

    fun getUserEmail(): String? = pref.getString(USER_EMAIL, null)

    fun getUserName(): String? = pref.getString(USER_NAME, null)

    fun logout() {
        pref.edit().clear().apply()
    }

    // Simpan history
    fun addHistoryItem(historyItem: HistoryItem) {
        val historyList = getHistoryList().toMutableList()
        historyList.add(historyItem)
        val json = Gson().toJson(historyList)
        pref.edit().putString(HISTORY_LIST, json).apply()
    }

    // Ambil daftar history
    fun getHistoryList(): List<HistoryItem> {
        val json = pref.getString(HISTORY_LIST, null)
        return if (json != null) {
            val type = object : TypeToken<List<HistoryItem>>() {}.type
            Gson().fromJson(json, type)
        } else {
            emptyList()
        }
    }
}


