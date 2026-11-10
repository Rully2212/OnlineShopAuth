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
        binding.usernameInput.setText(session.username)
        binding.roleText.text = "Role: ${session.role}"
        binding.backButton.setOnClickListener { finish() }
        binding.saveButton.setOnClickListener { submitProfile() }
        binding.logoutButton.setOnClickListener { viewModel.logout() }
        viewModel.event.observe(this) { event ->
            when (event) {
                AuthEvent.Loading -> showLoading(true)
                AuthEvent.LoggedOut -> SessionRouter.openLogin(this)
                is AuthEvent.ProfileUpdated -> {
                    showLoading(false)
                    binding.usernameText.text = event.username
                    binding.usernameInput.setText(event.username)
                    binding.oldPasswordInput.text?.clear()
                    binding.newPasswordInput.text?.clear()
                    binding.errorText.isVisible = false
                    binding.successText.text = event.message
                    binding.successText.isVisible = true
                }
                is AuthEvent.Error -> {
                    showLoading(false)
                    binding.successText.isVisible = false
                    binding.errorText.text = event.message
                    binding.errorText.isVisible = true
                }
                else -> Unit
            }
        }
    }

    private fun submitProfile() {
        val username = binding.usernameInput.text.toString().trim()
        val oldPassword = binding.oldPasswordInput.text.toString()
        val newPassword = binding.newPasswordInput.text.toString()
        binding.successText.isVisible = false
        binding.errorText.isVisible = false

        val error = when {
            username.isBlank() -> "Username is required."
            oldPassword.isBlank() != newPassword.isBlank() ->
                "Enter both the current password and the new password."
            newPassword.isNotBlank() && newPassword.length < 6 ->
                "The new password must contain at least 6 characters."
            else -> null
        }
        if (error != null) {
            binding.errorText.text = error
            binding.errorText.isVisible = true
            return
        }
        viewModel.updateProfile(username, oldPassword, newPassword)
    }

    private fun showLoading(loading: Boolean) {
        binding.loadingBar.isVisible = loading
        binding.saveButton.isEnabled = !loading
        binding.logoutButton.isEnabled = !loading
    }
}
