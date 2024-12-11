package com.example.helotani.ui.home

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.helotani.R
import com.example.helotani.data.pref.DarkModePref
import com.example.helotani.data.pref.UserPref
import com.example.helotani.data.pref.dataStore
import com.example.helotani.databinding.ActivityHomeBinding
import com.example.helotani.ui.welcome.WelcomeActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var userPref: UserPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPref = UserPref(this)
        supportActionBar?.setBackgroundDrawable(ColorDrawable
            (ContextCompat.getColor(this, R.color.btn_login)))

        val pref = DarkModePref.getInstance(dataStore)
        lifecycleScope.launch {
            pref.getThemeSetting().collect { isDarkModeActive ->
                if (isDarkModeActive) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            }
        }

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_home)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_history,
                R.id.navigation_setting,
                R.id.navigation_profile
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: android.view.Menu?): Boolean {
        menuInflater.inflate(R.menu.item_logout, menu)
        return true
    }
    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        return when (item.itemId) {
            R.id.btn_logout -> {
                // Lakukan tindakan logout di sini
                logoutActivity()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun logoutActivity() {
        // Hapus data pengguna dari SharedPreferences atau dataStore
        // Pindahkan pengguna ke halaman login lagi
        userPref.logout()
        val intent = Intent(this, WelcomeActivity::class.java)
        startActivity(intent)
        finish()  // Tutup HomeActivity
    }
}