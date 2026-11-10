package com.example.onlineshopauth.data

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CartResponseParsingTest {
    private val gson = Gson()

    @Test
    fun parsesCartListWithNumericTotals() {
        val json = """{
            "status": true,
            "code": 200,
            "message": "ok",
            "data": {
                "user_id": 4,
                "total_items": 1,
                "grand_total": 900000.00,
                "items": [{
                    "cart_id": 1,
                    "product_id": 2,
                    "nama_product": "Sepatu Badminton",
                    "harga": 450000.00,
                    "deskripsi": "Sepatu olahraga",
                    "gambar": "https://example.com/shoe.jpg",
                    "quantity": 2,
                    "subtotal": 900000.00,
                    "created_at": "2026-10-13 22:32:44"
                }]
            }
        }"""
        val type = TypeToken.getParameterized(ApiEnvelope::class.java, CartSummary::class.java).type
        val response: ApiEnvelope<CartSummary> = gson.fromJson(json, type)

        assertTrue(response.status)
        assertEquals("900000.00", response.data?.grandTotal)
        assertEquals(2, response.data?.items?.first()?.quantity)
        assertEquals("450000.00", response.data?.items?.first()?.harga)
    }

    @Test
    fun parsesAddToCartResponse() {
        val json = """{
            "cart_id": 3,
            "quantity": 4,
            "product_id": 7,
            "nama_product": "Sepatu Pantofel",
            "harga": "335000.00",
            "gambar": null,
            "total_harga": 1340000
        }"""
        val data = gson.fromJson(json, CartAddData::class.java)

        assertEquals(3, data.cartId)
        assertEquals("Sepatu Pantofel", data.productName)
        assertEquals("1340000", data.totalPrice)
    }
}
