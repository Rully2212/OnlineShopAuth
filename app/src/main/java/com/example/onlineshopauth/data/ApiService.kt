package com.example.onlineshopauth.data

import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

data class ApiEnvelope<T>(
    val status: Boolean,
    val code: Int,
    val message: String,
    val data: T?
)

data class AuthData(
    val id: Int,
    val username: String,
    val email: String?,
    val alamat: String?,
    val no_whatsapp: String?,
    val role: String,
    val token: String?
)

interface ApiService {
    @FormUrlEncoded
    @POST("auth/login.php")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): Response<ApiEnvelope<AuthData>>

    @FormUrlEncoded
    @POST("auth/register.php")
    suspend fun register(
        @Field("username") username: String,
        @Field("email") email: String,
        @Field("alamat") address: String,
        @Field("no_whatsapp") whatsapp: String,
        @Field("password") password: String
    ): Response<ApiEnvelope<AuthData>>

    @POST("auth/logout.php")
    suspend fun logout(@Header("Authorization") authorization: String): Response<ApiEnvelope<Unit>>

    @GET("products/listproduct.php")
    suspend fun listProducts(
        @Header("Authorization") authorization: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Response<ApiEnvelope<ProductCollection>>

    @GET("products/search.php")
    suspend fun searchProducts(
        @Header("Authorization") authorization: String,
        @Query("q") query: String
    ): Response<ApiEnvelope<ProductCollection>>

    @GET("products/detail.php")
    suspend fun productDetail(
        @Header("Authorization") authorization: String,
        @Query("id") productId: Int
    ): Response<ApiEnvelope<ProductItem>>
}
