package com.example.onlineshopauth.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.onlineshopauth.data.SessionManager
import com.example.onlineshopauth.databinding.ActivityCartBinding
import java.text.NumberFormat
import java.util.Locale

class CartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCartBinding
    private lateinit var viewModel: CartViewModel
    private lateinit var token: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val manager = SessionManager(this)
        val session = manager.current()
        if (session?.role != "user") {
            SessionRouter.openDestination(this, manager)
            return
        }
        token = session.token
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[CartViewModel::class.java]

        val adapter = CartAdapter()
        binding.cartList.layoutManager = LinearLayoutManager(this)
        binding.cartList.adapter = adapter
        binding.backButton.setOnClickListener { finish() }
        binding.retryButton.setOnClickListener { viewModel.load(token) }

        viewModel.state.observe(this) { state ->
            adapter.submitList(state.items)
            binding.loadingBar.visibility = if (state.loading) View.VISIBLE else View.GONE
            binding.cartList.visibility = if (state.items.isNotEmpty()) View.VISIBLE else View.GONE
            binding.summaryCard.visibility = if (state.items.isNotEmpty()) View.VISIBLE else View.GONE
            binding.emptyText.visibility = if (!state.loading && state.error == null && state.items.isEmpty()) {
                View.VISIBLE
            } else View.GONE
            binding.errorText.text = state.error.orEmpty()
            binding.errorText.visibility = if (state.error != null) View.VISIBLE else View.GONE
            binding.retryButton.visibility = if (state.error != null) View.VISIBLE else View.GONE
            binding.totalItemsText.text = "${state.totalItems} item(s)"
            binding.grandTotalText.text = "Grand total: ${rupiah(state.grandTotal)}"
        }
    }

    override fun onResume() {
        super.onResume()
        if (::viewModel.isInitialized) viewModel.load(token)
    }

    private fun rupiah(value: String): String {
        val amount = value.toDoubleOrNull() ?: return "Rp $value"
        return NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
            maximumFractionDigits = 0
        }.format(amount)
    }
}
