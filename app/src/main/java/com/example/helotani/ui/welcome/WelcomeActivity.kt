package com.example.helotani.ui.welcome

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.helotani.data.pref.UserPref
import com.example.helotani.databinding.ActivityWelcomeBinding
import com.example.helotani.ui.home.HomeActivity
import com.example.helotani.ui.home.HomeFragment
import com.example.helotani.ui.login.LoginActivity
import com.example.helotani.ui.register.RegisterActivity

class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding
    private lateinit var userPref: UserPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        userPref = UserPref(this)

        // Cek apakah user sudah login
        if (userPref.isLoggedIn()) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        binding.loginButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        binding.registerButton.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}
