package com.example.onlineshopauth.data

import com.example.onlineshopauth.BuildConfig
import com.google.gson.JsonParser
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiException(message: String) : Exception(message)

class AuthRepository(private val service: ApiService) {
    suspend fun login(username: String, password: String): AuthData {
        val data = requireData(service.login(username, password))
        if (data.token.isNullOrBlank() || data.role !in setOf("admin", "user")) {
            throw ApiException("The login response has no valid token or role.")
        }
        return data
    }

    suspend fun register(
        username: String, email: String, address: String, whatsapp: String, password: String
    ): AuthData = requireData(service.register(username, email, address, whatsapp, password))

    suspend fun logout(token: String) {
        val response = service.logout("Bearer $token")
        if (!response.isSuccessful || response.body()?.status != true) {
            throw ApiException(errorMessage(response))
        }
    }

    private fun <T> requireData(response: Response<ApiEnvelope<T>>): T {
        if (!response.isSuccessful || response.body()?.status != true) {
            throw ApiException(errorMessage(response))
        }
        return response.body()?.data ?: throw ApiException("The server returned no data.")
    }

    private fun <T> errorMessage(response: Response<ApiEnvelope<T>>): String {
        response.body()?.message?.takeIf { it.isNotBlank() }?.let { return it }
        return try {
            val raw = response.errorBody()?.string().orEmpty()
            JsonParser.parseString(raw).asJsonObject.get("message")?.asString
                ?: "Request failed (HTTP ${response.code()})."
        } catch (_: Exception) {
            "Request failed (HTTP ${response.code()})."
        }
    }

    companion object {
        fun create(): AuthRepository {
            val retrofit = Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return AuthRepository(retrofit.create(ApiService::class.java))
        }
    }
}
