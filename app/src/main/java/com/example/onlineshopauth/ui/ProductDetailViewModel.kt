package com.example.onlineshopauth.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onlineshopauth.data.ProductItem
import com.example.onlineshopauth.data.ProductRepository
import kotlinx.coroutines.launch

data class ProductDetailState(
    val product: ProductItem? = null,
    val loading: Boolean = false,
    val error: String? = null
)

class ProductDetailViewModel : ViewModel() {
    private val repository = ProductRepository.create()
    private val _state = MutableLiveData(ProductDetailState())
    val state: LiveData<ProductDetailState> = _state
    var hasRequested = false
        private set

    fun load(token: String, productId: Int) {
        hasRequested = true
        _state.value = ProductDetailState(loading = true)
        viewModelScope.launch {
            try {
                _state.value = ProductDetailState(product = repository.detail(token, productId))
            } catch (e: Exception) {
                _state.value = ProductDetailState(error = e.message ?: "Could not load product details.")
            }
        }
    }
}
