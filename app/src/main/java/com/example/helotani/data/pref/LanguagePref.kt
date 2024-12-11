package com.example.helotani.data.pref

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LanguagePref private constructor(private val dataStore: DataStore<Preferences>) {

    // Key untuk preferensi bahasa
    private val LANGUAGE_KEY = stringPreferencesKey("language_setting")

    // Fungsi untuk mendapatkan preferensi bahasa
    fun getLanguageSetting(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[LANGUAGE_KEY] ?: "en" // Default bahasa Inggris
        }
    }

    // Fungsi untuk menyimpan preferensi bahasa
    suspend fun saveLanguageSetting(language: String) {
        dataStore.edit { preferences ->
            preferences[LANGUAGE_KEY] = language
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: LanguagePref? = null

        fun getInstance(dataStore: DataStore<Preferences>): LanguagePref {
            return INSTANCE ?: synchronized(this) {
                val instance = LanguagePref(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}
