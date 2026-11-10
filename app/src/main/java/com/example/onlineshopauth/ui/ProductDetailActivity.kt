package com.example.onlineshopauth.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.onlineshopauth.data.SessionManager
import com.example.onlineshopauth.databinding.ActivityProductDetailBinding

class ProductDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductDetailBinding
    private lateinit var viewModel: ProductDetailViewModel
    private lateinit var addToCartViewModel: AddToCartViewModel
    private var quantity = 1

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
        addToCartViewModel = ViewModelProvider(this)[AddToCartViewModel::class.java]
        binding.backButton.setOnClickListener { finish() }
        binding.retryButton.setOnClickListener { viewModel.load(session.token, productId) }
        binding.decreaseButton.setOnClickListener {
            if (quantity > 1) quantity--
            updateQuantity()
        }
        binding.increaseButton.setOnClickListener {
            if (quantity < 99) quantity++
            updateQuantity()
        }
        binding.addToCartButton.setOnClickListener {
            addToCartViewModel.add(session.token, productId, quantity)
        }
        updateQuantity()
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
        addToCartViewModel.state.observe(this) { state ->
            binding.addToCartButton.isEnabled = !state.loading
            binding.addToCartButton.text = if (state.loading) "Adding..." else "Add to Cart"
            val status = state.message ?: state.error
            binding.cartStatusText.text = status.orEmpty()
            binding.cartStatusText.visibility = if (status != null) View.VISIBLE else View.GONE
            binding.cartStatusText.setTextColor(
                ContextCompat.getColor(
                    this,
                    if (state.error != null) com.example.onlineshopauth.R.color.brand_red_dark
                    else com.example.onlineshopauth.R.color.text_dark
                )
            )
        }
        if (!viewModel.hasRequested) viewModel.load(session.token, productId)
    }

    private fun updateQuantity() {
        binding.quantityText.text = quantity.toString()
        binding.decreaseButton.isEnabled = quantity > 1
        binding.increaseButton.isEnabled = quantity < 99
    }

    companion object {
        const val EXTRA_PRODUCT_ID = "product_id"
    }
}
