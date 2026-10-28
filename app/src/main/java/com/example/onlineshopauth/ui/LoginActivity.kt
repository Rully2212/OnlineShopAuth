package com.example.onlineshopauth.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.onlineshopauth.data.SessionManager
import com.example.onlineshopauth.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val sessionManager by lazy { SessionManager(this) }
    private val viewModel by viewModels<AuthViewModel> { AuthViewModelFactory(sessionManager) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginButton.setOnClickListener {
            val username = binding.usernameInput.text.toString().trim()
            val password = binding.passwordInput.text.toString()
            if (username.isBlank() || password.isBlank()) {
                showError("Username and password are required.")
            } else {
                binding.errorText.isVisible = false
                viewModel.login(username, password)
            }
        }
        binding.registerLink.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        viewModel.event.observe(this) { event ->
            when (event) {
                AuthEvent.Loading -> binding.loginButton.isEnabled = false
                AuthEvent.Authenticated -> SessionRouter.openDestination(this, sessionManager)
                is AuthEvent.Error -> {
                    binding.loginButton.isEnabled = true
                    showError(event.message)
                }
                else -> Unit
            }
        }
    }

    private fun showError(message: String) {
        binding.errorText.text = message
        binding.errorText.isVisible = true
    }
}
