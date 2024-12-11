package com.example.helotani.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.helotani.data.api.ApiConfig
import com.example.helotani.data.api.LoginRequest
import com.example.helotani.data.pref.UserPref
import com.example.helotani.databinding.ActivityLoginBinding
import com.example.helotani.ui.home.HomeActivity
import kotlinx.coroutines.launch
import kotlin.math.log

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var userPref: UserPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPref = UserPref(this)
        supportActionBar?.hide()

        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            if (email.isEmpty()) {
                binding.emailEditTextLayout.error = "Email tidak boleh kosong"
            } else if (password.isEmpty()) {
                binding.passwordEditTextLayout.error = "Password tidak boleh kosong"
            } else {
                binding.emailEditTextLayout.error = null
                binding.passwordEditTextLayout.error = null
                performLogin(email, password)
            }
        }
    }

    private fun performLogin(email: String, password: String) {
        lifecycleScope.launch {
            try {
                val response = ApiConfig.getLoginApiService().login(LoginRequest(email, password))
                Toast.makeText(this@LoginActivity, response.message, Toast.LENGTH_SHORT).show()

                // Simpan status login dan data user
                val userName = response.user.nama_user
                userPref.setLoginStatus(true, email, userName)

                val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            } catch (e: Exception) {
                Log.e("LoginActivity","${e.message}")
                Toast.makeText(this@LoginActivity, "Login gagal!!", Toast.LENGTH_LONG).show()
            }
        }
    }

}