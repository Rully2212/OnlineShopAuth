package com.example.onlineshopauth.data

import com.google.gson.annotations.SerializedName

data class CartAddData(
    @SerializedName("cart_id") val cartId: Int?,
    val quantity: Int?,
    @SerializedName("product_id") val productId: Int?,
    @SerializedName("nama_product") val productName: String?,
    val harga: String?,
    val gambar: String?,
    @SerializedName("total_harga") val totalPrice: String?
)

data class CartSummary(
    @SerializedName("user_id") val userId: Int?,
    @SerializedName("total_items") val totalItems: Int?,
    @SerializedName("grand_total") val grandTotal: String?,
    val items: List<CartItem>?
)

data class CartItem(
    @SerializedName("cart_id") val cartId: Int,
    @SerializedName("product_id") val productId: Int,
    @SerializedName("nama_product") val productName: String,
    val harga: String,
    val deskripsi: String?,
    val gambar: String?,
    val quantity: Int,
    val subtotal: String,
    @SerializedName("created_at") val createdAt: String?
)
