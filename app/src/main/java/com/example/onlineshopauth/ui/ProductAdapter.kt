package com.example.onlineshopauth.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.onlineshopauth.data.ProductItem
import com.example.onlineshopauth.databinding.ItemProductBinding

class ProductAdapter(private val onClick: (ProductItem) -> Unit) :
    ListAdapter<ProductItem, ProductAdapter.Holder>(DIFF) {

    class Holder(val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder = Holder(
        ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val product = getItem(position)
        holder.binding.productName.text = product.name
        holder.binding.productPrice.text = "Rp ${product.price}"
        Glide.with(holder.itemView)
            .load(product.imageUrl)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .error(android.R.drawable.ic_menu_gallery)
            .centerCrop()
            .into(holder.binding.productImage)
        holder.itemView.setOnClickListener { onClick(product) }
    }

    private companion object {
        val DIFF = object : DiffUtil.ItemCallback<ProductItem>() {
            override fun areItemsTheSame(oldItem: ProductItem, newItem: ProductItem) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: ProductItem, newItem: ProductItem) =
                oldItem == newItem
        }
    }
}
