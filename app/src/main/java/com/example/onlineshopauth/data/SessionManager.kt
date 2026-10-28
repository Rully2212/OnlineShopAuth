package com.example.onlineshopauth.data

import android.content.Context

data class Session(val token: String, val username: String, val role: String)

class SessionManager(context: Context) {
    private val preferences = context.applicationContext.getSharedPreferences("auth_session", Context.MODE_PRIVATE)

    fun save(data: AuthData) {
        val token = data.token?.takeIf { it.isNotBlank() } ?: throw ApiException("Token kosong.")
        if (data.username.isBlank()) throw ApiException("Username kosong.")
        if (data.role !in setOf("admin", "user")) throw ApiException("Role tidak dikenal.")
        preferences.edit()
            .putString("token", token)
            .putString("username", data.username)
            .putString("role", data.role)
            .commit()
    }

    fun current(): Session? {
        val token = preferences.getString("token", null)
        val username = preferences.getString("username", null)
        val role = preferences.getString("role", null)
        return if (!token.isNullOrBlank() && !username.isNullOrBlank() && role in setOf("admin", "user")) {
            Session(token, username, role!!)
        } else null
    }

    fun clear() {
        preferences.edit().clear().commit()
    }
}
