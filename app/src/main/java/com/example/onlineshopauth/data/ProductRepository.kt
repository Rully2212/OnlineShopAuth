package com.example.onlineshopauth.data

import com.example.onlineshopauth.BuildConfig
import com.google.gson.JsonParser
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ProductRepository(private val service: ApiService) {
    suspend fun list(token: String, page: Int, limit: Int): ProductCollection =
        requireData(service.listProducts("Bearer $token", page, limit))

    suspend fun search(token: String, query: String): ProductCollection =
        requireData(service.searchProducts("Bearer $token", query))

    suspend fun detail(token: String, productId: Int): ProductItem =
        requireData(service.productDetail("Bearer $token", productId))

    private fun <T> requireData(response: Response<ApiEnvelope<T>>): T {
        if (!response.isSuccessful || response.body()?.status != true) {
            throw ApiException(errorMessage(response))
        }
        return response.body()?.data ?: throw ApiException("The server returned no product data.")
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
        fun create(): ProductRepository {
            val service = Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
            return ProductRepository(service)
        }
    }
}
