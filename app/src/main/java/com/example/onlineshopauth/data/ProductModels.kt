package com.example.onlineshopauth.data

import com.google.gson.annotations.SerializedName

data class ProductItem(
    val id: Int,
    @SerializedName("nama_product") val name: String,
    @SerializedName("harga") val price: String,
    @SerializedName("deskripsi") val description: String,
    @SerializedName("gambar") val imageUrl: String?,
    @SerializedName("created_at") val createdAt: String?
)

data class ProductCollection(
    val products: List<ProductItem>?,
    val pagination: ProductPagination?,
    val keyword: String?,
    @SerializedName("total_found") val totalFound: Int?
)

data class ProductPagination(
    @SerializedName("current_page") val currentPage: Int,
    @SerializedName("per_page") val perPage: Int,
    @SerializedName("total_data") val totalData: Int,
    @SerializedName("total_pages") val totalPages: Int
)
