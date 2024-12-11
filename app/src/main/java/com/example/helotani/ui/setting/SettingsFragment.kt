package com.example.helotani.ui.setting

import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import com.example.helotani.data.pref.DarkModePref
import com.example.helotani.data.pref.LanguagePref
import com.example.helotani.data.pref.dataStore
import com.example.helotani.databinding.FragmentSettingsBinding
import java.util.*

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val darkModePref = DarkModePref.getInstance(requireContext().dataStore)
        val languagePref = LanguagePref.getInstance(requireContext().dataStore)

        // Pass both darkModePref and languagePref to the ViewModel
        val mainViewModel = ViewModelProvider(this, ViewModelFactory(darkModePref, languagePref)).get(SettingViewModel::class.java)

        setupDarkModeFeature(mainViewModel)
        setupLanguageFeature(mainViewModel)

        return root
    }

    private fun setupDarkModeFeature(mainViewModel: SettingViewModel) {
        val switchTheme = binding.switchTheme

        mainViewModel.getThemeSettings().observe(viewLifecycleOwner) { isDarkModeActive: Boolean ->
            val currentMode = AppCompatDelegate.getDefaultNightMode()
            val newMode = if (isDarkModeActive) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO

            if (currentMode != newMode) {
                applyTheme(isDarkModeActive)
            }
            switchTheme.isChecked = isDarkModeActive
            switchTheme.setOnCheckedChangeListener { _, isChecked ->
                mainViewModel.saveThemeSetting(isChecked) {
                    applyTheme(isChecked)
                }
            }
        }
    }

    private fun setupLanguageFeature(mainViewModel: SettingViewModel) {
        val spinnerLanguage = binding.spinnerLanguage

        mainViewModel.getLanguageSetting().observe(viewLifecycleOwner) { savedLanguage ->
            val position = when (savedLanguage) {
                "en" -> 0 // English
                "id" -> 1 // Bahasa Indonesia
                else -> 0
            }
            spinnerLanguage.setSelection(position)

            spinnerLanguage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val selectedLanguage = when (position) {
                        0 -> "en"
                        1 -> "id"
                        else -> "en"
                    }
                    if (selectedLanguage != savedLanguage) {
                        mainViewModel.saveLanguageSetting(selectedLanguage) {
                            setLocale(selectedLanguage)
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }
    }

    private fun setLocale(language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val config = Configuration()
        config.setLocale(locale)

        // Update resources dengan konfigurasi baru
        requireContext().resources.updateConfiguration(config, requireContext().resources.displayMetrics)

        // Menggunakan recreate() untuk memaksa fragment/activity me-refresh dengan bahasa baru
        requireActivity().recreate() // Akan memicu activity untuk di-recreate dengan locale baru
    }


    private fun applyTheme(isDarkModeActive: Boolean) {
        if (isAdded) {
            val currentMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            if ((isDarkModeActive && currentMode != Configuration.UI_MODE_NIGHT_YES) ||
                (!isDarkModeActive && currentMode != Configuration.UI_MODE_NIGHT_NO)) {
                AppCompatDelegate.setDefaultNightMode(
                    if (isDarkModeActive) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}