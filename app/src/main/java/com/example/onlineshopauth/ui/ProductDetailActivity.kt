package com.example.onlineshopauth.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.onlineshopauth.data.SessionManager
import com.example.onlineshopauth.databinding.ActivityProductDetailBinding

class ProductDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductDetailBinding
    private lateinit var viewModel: ProductDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val manager = SessionManager(this)
        val session = manager.current()
        if (session?.role != "user") {
            SessionRouter.openDestination(this, manager)
            return
        }
        val productId = intent.getIntExtra(EXTRA_PRODUCT_ID, -1)
        if (productId <= 0) {
            finish()
            return
        }

        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[ProductDetailViewModel::class.java]
        binding.backButton.setOnClickListener { finish() }
        binding.retryButton.setOnClickListener { viewModel.load(session.token, productId) }
        viewModel.state.observe(this) { state ->
            binding.loadingBar.visibility = if (state.loading) View.VISIBLE else View.GONE
            binding.contentScroll.visibility = if (state.product != null) View.VISIBLE else View.GONE
            binding.errorText.text = state.error
            binding.errorText.visibility = if (state.error != null) View.VISIBLE else View.GONE
            binding.retryButton.visibility = if (state.error != null) View.VISIBLE else View.GONE
            state.product?.let { product ->
                binding.productName.text = product.name
                binding.productPrice.text = "Rp ${product.price}"
                binding.productDescription.text = product.description
                binding.createdAtText.text = product.createdAt?.let { "Added: $it" } ?: ""
                Glide.with(this)
                    .load(product.imageUrl)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_gallery)
                    .into(binding.productImage)
            }
        }
        if (!viewModel.hasRequested) viewModel.load(session.token, productId)
    }

    companion object {
        const val EXTRA_PRODUCT_ID = "product_id"
    }
}
