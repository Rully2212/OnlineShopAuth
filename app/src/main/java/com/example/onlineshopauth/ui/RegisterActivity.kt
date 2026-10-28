package com.example.onlineshopauth.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.onlineshopauth.data.SessionManager
import com.example.onlineshopauth.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val sessionManager by lazy { SessionManager(this) }
    private val viewModel by viewModels<AuthViewModel> { AuthViewModelFactory(sessionManager) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.registerButton.setOnClickListener {
            val username = binding.usernameInput.text.toString().trim()
            val email = binding.emailInput.text.toString().trim()
            val address = binding.addressInput.text.toString().trim()
            val whatsapp = binding.whatsappInput.text.toString().trim()
            val password = binding.passwordInput.text.toString()
            when {
                listOf(username, email, address, whatsapp, password).any { it.isBlank() } ->
                    showError("Semua kolom wajib diisi.")
                !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                    showError("Format email tidak valid.")
                else -> {
                    binding.errorText.isVisible = false
                    viewModel.registerAndLogin(username, email, address, whatsapp, password)
                }
            }
        }
        binding.loginLink.setOnClickListener { finish() }
        viewModel.event.observe(this) { event ->
            when (event) {
                AuthEvent.Loading -> binding.registerButton.isEnabled = false
                AuthEvent.Authenticated -> SessionRouter.openDestination(this, sessionManager)
                is AuthEvent.Error -> {
                    binding.registerButton.isEnabled = true
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
