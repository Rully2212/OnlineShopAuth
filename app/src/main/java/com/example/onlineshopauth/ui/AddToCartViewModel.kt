package com.example.onlineshopauth.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onlineshopauth.data.CartRepository
import kotlinx.coroutines.launch

data class AddToCartState(
    val loading: Boolean = false,
    val message: String? = null,
    val error: String? = null
)

class AddToCartViewModel : ViewModel() {
    private val repository = CartRepository.create()
    private val _state = MutableLiveData(AddToCartState())
    val state: LiveData<AddToCartState> = _state

    fun add(token: String, productId: Int, quantity: Int) {
        if (_state.value?.loading == true) return
        _state.value = AddToCartState(loading = true)
        viewModelScope.launch {
            try {
                val result = repository.add(token, productId, quantity)
                val name = result.productName?.takeIf { it.isNotBlank() } ?: "Product"
                _state.value = AddToCartState(
                    message = "$name added to cart (quantity: ${result.quantity ?: quantity})."
                )
            } catch (e: Exception) {
                _state.value = AddToCartState(error = e.message ?: "Could not add product to cart.")
            }
        }
    }
}
