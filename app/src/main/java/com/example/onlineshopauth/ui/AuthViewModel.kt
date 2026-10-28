package com.example.onlineshopauth.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.onlineshopauth.data.AuthRepository
import com.example.onlineshopauth.data.SessionManager
import kotlinx.coroutines.launch

sealed class AuthEvent {
    data object Loading : AuthEvent()
    data object Authenticated : AuthEvent()
    data object LoggedOut : AuthEvent()
    data class Error(val message: String) : AuthEvent()
}

class AuthViewModel(
    private val repository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {
    private val _event = MutableLiveData<AuthEvent>()
    val event: LiveData<AuthEvent> = _event

    fun login(username: String, password: String) = viewModelScope.launch {
        _event.value = AuthEvent.Loading
        try {
            sessionManager.save(repository.login(username, password))
            _event.value = AuthEvent.Authenticated
        } catch (e: Exception) {
            _event.value = AuthEvent.Error(e.message ?: "Login failed. Please try again.")
        }
    }

    fun registerAndLogin(
        username: String, email: String, address: String, whatsapp: String, password: String
    ) = viewModelScope.launch {
        _event.value = AuthEvent.Loading
        try {
            repository.register(username, email, address, whatsapp, password)
            // Registration does not return a token, so log in to create a session.
            sessionManager.save(repository.login(username, password))
            _event.value = AuthEvent.Authenticated
        } catch (e: Exception) {
            _event.value = AuthEvent.Error(e.message ?: "Registration failed. Try logging in if the account was created.")
        }
    }

    fun logout() = viewModelScope.launch {
        _event.value = AuthEvent.Loading
        val token = sessionManager.current()?.token
        try {
            if (token != null) repository.logout(token)
        } catch (_: Exception) {
            // Clear the local session even when the server is unreachable.
        } finally {
            sessionManager.clear()
            _event.value = AuthEvent.LoggedOut
        }
    }
}

class AuthViewModelFactory(private val sessionManager: SessionManager) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AuthViewModel(AuthRepository.create(), sessionManager) as T
    }
}
