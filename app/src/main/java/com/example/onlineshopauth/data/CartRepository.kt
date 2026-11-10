package com.example.onlineshopauth.data

import com.example.onlineshopauth.BuildConfig
import com.google.gson.JsonParser
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CartRepository(private val service: ApiService) {
    suspend fun add(token: String, productId: Int, quantity: Int): CartAddData =
        requireData(service.addToCart("Bearer $token", productId, quantity))

    suspend fun list(token: String): CartSummary =
        requireData(service.cartList("Bearer $token"))

    private fun <T> requireData(response: Response<ApiEnvelope<T>>): T {
        if (!response.isSuccessful || response.body()?.status != true) {
            throw ApiException(errorMessage(response))
        }
        return response.body()?.data ?: throw ApiException("The server returned no cart data.")
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
        fun create(): CartRepository {
            val service = Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
            return CartRepository(service)
        }
    }
}
