package com.example.helotani.ui.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.helotani.data.pref.DarkModePref
import com.example.helotani.data.pref.LanguagePref

class ViewModelFactory(
    private val darkModePref: DarkModePref,
    private val languagePref: LanguagePref
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingViewModel::class.java)) {
            return SettingViewModel(darkModePref, languagePref) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}