package com.hustlers.grocekit.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.hustlers.grocekit.R
import com.hustlers.grocekit.adapter.CartAdapter
import com.hustlers.grocekit.databinding.ActivityCartBinding
import com.hustlers.grocekit.viewmodel.CartViewModel

class CartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCartBinding
    private lateinit var cartViewModel: CartViewModel
    private lateinit var cartAdapter: CartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_cart)
        cartViewModel = ViewModelProvider(this)[CartViewModel::class.java]

        binding.viewModel = cartViewModel
        binding.lifecycleOwner = this

        setupRecyclerView()
        setupObservers()
        setupClickListeners()

        loadCartItems()
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(
            onQuantityChange = { cartItem, newQuantity ->
                cartViewModel.updateQuantity(cartItem, newQuantity)
            },
            onRemoveItem = { cartItem ->
                cartViewModel.removeItem(cartItem)
            }
        )

        binding.cartRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@CartActivity)
            adapter = cartAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupObservers() {
        cartViewModel.cartItems.observe(this) { items ->
            cartAdapter.submitList(items)

            if (items.isNullOrEmpty()) {
                binding.emptyState.visibility = View.VISIBLE
                binding.cartRecyclerView.visibility = View.GONE
                binding.checkoutButton.isEnabled = false
            } else {
                binding.emptyState.visibility = View.GONE
                binding.cartRecyclerView.visibility = View.VISIBLE
                binding.checkoutButton.isEnabled = true
            }
        }

        cartViewModel.totalAmount.observe(this) { total ->
            binding.totalAmountText.text = "₹${String.format("%.2f", total)}"
        }
    }

    private fun setupClickListeners() {
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        binding.checkoutButton.setOnClickListener {
            val intent = Intent(this, CheckoutActivity::class.java).apply {
                putExtra("totalAmount", cartViewModel.totalAmount.value ?: 0.0)

            }
            startActivity(intent)
        }
    }

    private fun loadCartItems() {
    }
}