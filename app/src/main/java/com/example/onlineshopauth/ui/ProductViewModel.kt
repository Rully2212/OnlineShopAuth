package com.example.onlineshopauth.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onlineshopauth.data.ProductItem
import com.example.onlineshopauth.data.ProductRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

data class ProductListState(
    val products: List<ProductItem> = emptyList(),
    val loading: Boolean = false,
    val loadingMore: Boolean = false,
    val error: String? = null,
    val hasMore: Boolean = false
)

class ProductViewModel : ViewModel() {
    private val repository = ProductRepository.create()
    private val _state = MutableLiveData(ProductListState())
    val state: LiveData<ProductListState> = _state
    var hasRequested = false
        private set

    private var request: Job? = null
    private var currentPage = 0
    private var totalPages = 1
    private var activeQuery: String? = null
    private var loadingMore = false

    fun refresh(token: String, query: String? = null) {
        hasRequested = true
        request?.cancel()
        activeQuery = query?.trim()?.takeIf { it.isNotEmpty() }
        currentPage = 0
        totalPages = 1
        loadingMore = false
        _state.value = ProductListState(loading = true)
        request = viewModelScope.launch {
            try {
                val result = activeQuery?.let { repository.search(token, it) }
                    ?: repository.list(token, page = 1, limit = PAGE_SIZE)
                currentPage = result.pagination?.currentPage ?: 1
                totalPages = result.pagination?.totalPages ?: 1
                _state.value = ProductListState(
                    products = result.products.orEmpty(),
                    hasMore = activeQuery == null && currentPage < totalPages
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.value = ProductListState(error = e.message ?: "Could not load products.")
            }
        }
    }

    fun loadNext(token: String) {
        val previous = _state.value ?: return
        if (activeQuery != null || loadingMore || previous.loading || !previous.hasMore) return
        loadingMore = true
        _state.value = previous.copy(loadingMore = true, error = null)
        request = viewModelScope.launch {
            try {
                val result = repository.list(token, page = currentPage + 1, limit = PAGE_SIZE)
                currentPage = result.pagination?.currentPage ?: currentPage + 1
                totalPages = result.pagination?.totalPages ?: currentPage
                _state.value = ProductListState(
                    products = previous.products + result.products.orEmpty(),
                    hasMore = currentPage < totalPages
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.value = previous.copy(
                    loadingMore = false,
                    error = e.message ?: "Could not load more products."
                )
            } finally {
                loadingMore = false
            }
        }
    }

    fun retry(token: String) {
        val current = _state.value ?: ProductListState()
        if (current.products.isNotEmpty() && current.hasMore) loadNext(token)
        else refresh(token, activeQuery)
    }

    private companion object {
        const val PAGE_SIZE = 5
    }
}
