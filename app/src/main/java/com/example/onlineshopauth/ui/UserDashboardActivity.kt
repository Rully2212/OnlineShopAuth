package com.example.onlineshopauth.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.onlineshopauth.data.SessionManager
import com.example.onlineshopauth.databinding.ActivityUserDashboardBinding

class UserDashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserDashboardBinding
    private lateinit var viewModel: ProductViewModel
    private lateinit var token: String
    private var searching = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val manager = SessionManager(this)
        val session = manager.current()
        if (session?.role != "user") {
            SessionRouter.openDestination(this, manager)
            return
        }
        token = session.token
        binding = ActivityUserDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[ProductViewModel::class.java]

        binding.greetingText.text = "Hello, ${session.username}"
        binding.profileButton.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        val productAdapter = ProductAdapter { product ->
            startActivity(Intent(this, ProductDetailActivity::class.java).apply {
                putExtra(ProductDetailActivity.EXTRA_PRODUCT_ID, product.id)
            })
        }
        val layoutManager = LinearLayoutManager(this)
        binding.productList.layoutManager = layoutManager
        binding.productList.adapter = productAdapter
        binding.productList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0 && layoutManager.findLastVisibleItemPosition() >= productAdapter.itemCount - 2) {
                    viewModel.loadNext(token)
                }
            }
        })

        binding.searchButton.setOnClickListener { submitSearch() }
        binding.searchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                submitSearch()
                true
            } else false
        }
        binding.searchInput.addTextChangedListener { text ->
            if (text.isNullOrBlank() && searching) {
                searching = false
                viewModel.refresh(token)
            }
        }
        binding.retryButton.setOnClickListener { viewModel.retry(token) }

        viewModel.state.observe(this) { state ->
            productAdapter.submitList(state.products)
            binding.loadingBar.visibility = if (state.loading) View.VISIBLE else View.GONE
            binding.loadMoreBar.visibility = if (state.loadingMore) View.VISIBLE else View.GONE
            binding.statusText.text = state.error ?: if (!state.loading && state.products.isEmpty()) {
                "No products found."
            } else ""
            binding.statusText.visibility = if (binding.statusText.text.isNotEmpty()) View.VISIBLE else View.GONE
            binding.retryButton.visibility = if (state.error != null) View.VISIBLE else View.GONE
        }
        if (!viewModel.hasRequested) viewModel.refresh(token)
    }

    private fun submitSearch() {
        val query = binding.searchInput.text.toString().trim()
        searching = query.isNotEmpty()
        viewModel.refresh(token, query)
    }
}
