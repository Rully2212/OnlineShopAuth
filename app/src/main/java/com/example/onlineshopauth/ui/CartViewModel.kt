package com.example.onlineshopauth.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onlineshopauth.data.CartItem
import com.example.onlineshopauth.data.CartRepository
import kotlinx.coroutines.launch

data class CartState(
    val items: List<CartItem> = emptyList(),
    val totalItems: Int = 0,
    val grandTotal: String = "0",
    val loading: Boolean = false,
    val error: String? = null
)

class CartViewModel : ViewModel() {
    private val repository = CartRepository.create()
    private val _state = MutableLiveData(CartState())
    val state: LiveData<CartState> = _state

    fun load(token: String) {
        if (_state.value?.loading == true) return
        _state.value = (_state.value ?: CartState()).copy(loading = true, error = null)
        viewModelScope.launch {
            try {
                val cart = repository.list(token)
                _state.value = CartState(
                    items = cart.items.orEmpty(),
                    totalItems = cart.totalItems ?: cart.items.orEmpty().size,
                    grandTotal = cart.grandTotal ?: "0"
                )
            } catch (e: Exception) {
                val previous = _state.value ?: CartState()
                _state.value = previous.copy(
                    loading = false,
                    error = e.message ?: "Could not load your cart."
                )
            }
        }
    }
}
