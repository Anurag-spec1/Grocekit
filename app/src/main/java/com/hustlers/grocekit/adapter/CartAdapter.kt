package com.hustlers.grocekit.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hustlers.grocekit.data.model.CartItem

class CartAdapter(
    private val onQuantityChange: (CartItem, Int) -> Unit,
    private val onRemoveItem: (CartItem) -> Unit
) : ListAdapter<CartItem, CartAdapter.CartViewHolder>(CartItemDiffCallback()) {

    inner class CartViewHolder(
        private val binding: ItemCartBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(cartItem: CartItem) {
            binding.apply {
                // Set data to views directly
                cartItemName.text = cartItem.product.name
                cartItemPrice.text = "₹${String.format("%.2f", cartItem.totalPrice)}"
                cartItemQuantity.text = cartItem.quantity.toString()

                // Load product image using Glide
                Glide.with(itemView.context)
                    .load(cartItem.product.imageUrl)
                    .placeholder(R.drawable.ic_product_placeholder)
                    .error(R.drawable.ic_product_placeholder)
                    .into(cartItemImage)

                // Increase quantity button
                increaseButton.setOnClickListener {
                    onQuantityChange(cartItem, cartItem.quantity + 1)
                }

                // Decrease quantity button
                decreaseButton.setOnClickListener {
                    if (cartItem.quantity > 1) {
                        onQuantityChange(cartItem, cartItem.quantity - 1)
                    } else {
                        onRemoveItem(cartItem)
                    }
                }

                // Remove item button
                removeButton.setOnClickListener {
                    onRemoveItem(cartItem)
                }

                executePendingBindings()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class CartItemDiffCallback : DiffUtil.ItemCallback<CartItem>() {
    override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
        return oldItem.product.id == newItem.product.id
    }

    override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
        return oldItem == newItem
    }
}