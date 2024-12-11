package com.example.helotani.ui.register

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.helotani.data.api.ApiConfig
import com.example.helotani.data.api.RegisterRequest
import com.example.helotani.databinding.ActivityRegisterBinding
import com.example.helotani.ui.welcome.WelcomeActivity
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.signupButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()
            val namaUser = binding.nameEditText.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty() && namaUser.isNotEmpty()) {
                registerUser(email, password, namaUser)
            } else {
                Toast.makeText(this, "Harap isi semua field", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun registerUser(email: String, password: String, namaUser: String) {
        binding.loadingProgressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                ApiConfig.getRegisterApiService().register(RegisterRequest(email, password, namaUser))
                Toast.makeText(this@RegisterActivity, "Registrasi berhasil!", Toast.LENGTH_SHORT).show()

                val intent = Intent(this@RegisterActivity, WelcomeActivity::class.java)
                startActivity(intent)
                finish()
            } catch (e: Exception) {
                Toast.makeText(this@RegisterActivity, "Registrasi gagal: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                binding.loadingProgressBar.visibility = View.GONE
            }
        }
    }
}
