package com.example.onlineshopauth.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.onlineshopauth.data.CartItem
import com.example.onlineshopauth.databinding.ItemCartBinding
import java.text.NumberFormat
import java.util.Locale

class CartAdapter : ListAdapter<CartItem, CartAdapter.Holder>(DIFF) {
    class Holder(val binding: ItemCartBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = Holder(
        ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = getItem(position)
        holder.binding.productName.text = item.productName
        holder.binding.productPrice.text = "${rupiah(item.harga)} each"
        holder.binding.quantityText.text = "Quantity: ${item.quantity}"
        holder.binding.subtotalText.text = "Subtotal: ${rupiah(item.subtotal)}"
        holder.binding.createdAtText.text = item.createdAt?.let { "Added: $it" }.orEmpty()
        Glide.with(holder.itemView)
            .load(item.gambar)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .error(android.R.drawable.ic_menu_gallery)
            .centerCrop()
            .into(holder.binding.productImage)
    }

    private fun rupiah(value: String): String {
        val amount = value.toDoubleOrNull() ?: return "Rp $value"
        return NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
            maximumFractionDigits = 0
        }.format(amount)
    }

    private companion object {
        val DIFF = object : DiffUtil.ItemCallback<CartItem>() {
            override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem) =
                oldItem.cartId == newItem.cartId

            override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem) =
                oldItem == newItem
        }
    }
}
