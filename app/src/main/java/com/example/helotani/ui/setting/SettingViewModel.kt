package com.example.helotani.ui.setting

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.helotani.data.pref.DarkModePref
import com.example.helotani.data.pref.LanguagePref
import kotlinx.coroutines.launch

class SettingViewModel(
    private val darkModePref: DarkModePref,
    private val languagePref: LanguagePref
) : ViewModel() {

    // Theme (Dark Mode) Settings
    fun getThemeSettings(): LiveData<Boolean> {
        return darkModePref.getThemeSetting().asLiveData()
    }

    fun saveThemeSetting(isDarkModeActive: Boolean, onComplete: () -> Unit) {
        viewModelScope.launch {
            darkModePref.saveThemeSetting(isDarkModeActive)
            onComplete()
        }
    }

    // Language Settings
    fun getLanguageSetting(): LiveData<String> {
        return languagePref.getLanguageSetting().asLiveData()
    }

    fun saveLanguageSetting(language: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            languagePref.saveLanguageSetting(language)
            onComplete()
        }
    }
}
