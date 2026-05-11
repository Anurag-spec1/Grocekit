package com.hustlers.grocekit.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.grocery.app.databinding.ItemProductBinding
import com.hustlers.grocekit.data.model.CartItem
import com.hustlers.grocekit.data.model.Product

class ProductAdapter(
    private val cartItems: List<CartItem>,
    private val onAddToCart: (Product) -> Unit,
    private val onQuantityChange: (Product, Int) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(private val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.product = product

            // Load image with Glide
            Glide.with(binding.root.context)
                .load(product.imageUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(binding.productImage)

            val cartItem = cartItems.find { it.product.id == product.id }
            val quantity = cartItem?.quantity ?: 0

            if (quantity > 0) {
                binding.addToCartButton.visibility = android.view.View.GONE
                binding.quantityControl.visibility = android.view.View.VISIBLE
                binding.quantityText.text = quantity.toString()
            } else {
                binding.addToCartButton.visibility = android.view.View.VISIBLE
                binding.quantityControl.visibility = android.view.View.GONE
            }

            binding.addToCartButton.setOnClickListener {
                onAddToCart(product)
            }

            binding.increaseButton.setOnClickListener {
                onQuantityChange(product, quantity + 1)
            }

            binding.decreaseButton.setOnClickListener {
                if (quantity > 0) {
                    onQuantityChange(product, quantity - 1)
                }
            }

            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {

    }

    override fun getItemCount() = 0

    fun submitList(products: List<Product>) {
    }
}