package com.example.onlineshopauth.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.onlineshopauth.data.SessionManager
import com.example.onlineshopauth.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private val sessionManager by lazy { SessionManager(this) }
    private val viewModel by viewModels<AuthViewModel> { AuthViewModelFactory(sessionManager) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val session = sessionManager.current()
        if (session == null) {
            SessionRouter.openLogin(this)
            return
        }
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.titleText.text = if (session.role == "admin") "Admin Profile" else "User Profile"
        binding.usernameText.text = session.username
        binding.roleText.text = "Role: ${session.role}"
        binding.logoutButton.setOnClickListener { viewModel.logout() }
        viewModel.event.observe(this) { event ->
            when (event) {
                AuthEvent.Loading -> binding.logoutButton.isEnabled = false
                AuthEvent.LoggedOut -> SessionRouter.openLogin(this)
                is AuthEvent.Error -> {
                    binding.logoutButton.isEnabled = true
                    binding.errorText.text = event.message
                    binding.errorText.isVisible = true
                }
                else -> Unit
            }
        }
    }
}
