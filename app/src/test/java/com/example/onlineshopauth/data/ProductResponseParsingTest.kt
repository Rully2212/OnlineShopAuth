package com.example.onlineshopauth.data

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ProductResponseParsingTest {
    private val gson = Gson()

    @Test
    fun parsesPaginatedListAndNumericPrice() {
        val json = """{
            "status": true,
            "code": 200,
            "message": "ok",
            "data": {
                "pagination": {"current_page": 1, "per_page": 5, "total_data": 6, "total_pages": 2},
                "products": [{"id": 7, "nama_product": "Sneakers", "harga": 321500.00,
                    "deskripsi": "Canvas shoes", "gambar": null, "created_at": "2026-10-28"}]
            }
        }"""
        val type = TypeToken.getParameterized(ApiEnvelope::class.java, ProductCollection::class.java).type
        val response: ApiEnvelope<ProductCollection> = gson.fromJson(json, type)

        assertTrue(response.status)
        assertEquals(2, response.data?.pagination?.totalPages)
        assertEquals("Sneakers", response.data?.products?.first()?.name)
        assertEquals("321500.00", response.data?.products?.first()?.price)
    }

    @Test
    fun parsesSearchAndDetailFields() {
        val search = gson.fromJson(
            """{"products":[],"keyword":"shoe","total_found":0}""",
            ProductCollection::class.java
        )
        val detail = gson.fromJson(
            """{"id":7,"nama_product":"Sneakers","harga":"321500.00",
                "deskripsi":"Canvas shoes","gambar":"https://example.com/shoe.jpg",
                "created_at":"2026-10-28"}""",
            ProductItem::class.java
        )

        assertEquals("shoe", search.keyword)
        assertEquals(0, search.totalFound)
        assertEquals("https://example.com/shoe.jpg", detail.imageUrl)
        assertEquals("2026-10-28", detail.createdAt)
    }
}
