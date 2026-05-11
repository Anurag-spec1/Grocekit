package com.hustlers.grocekit.activity


import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.hustlers.grocekit.R
import com.hustlers.grocekit.adapter.CategoryAdapter
import com.hustlers.grocekit.adapter.ProductAdapter
import com.hustlers.grocekit.databinding.ActivityHomeBinding
import com.hustlers.grocekit.viewmodel.HomeViewModel

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var productAdapter: ProductAdapter
    private lateinit var categoryAdapter: CategoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        binding.viewModel = homeViewModel
        binding.lifecycleOwner = this

        setupRecyclerViews()
        setupSearchView()
        observeData()
    }

    private fun setupRecyclerViews() {
        productAdapter = ProductAdapter(
            homeViewModel.cartItems.value ?: listOf(),
            onAddToCart = { product -> homeViewModel.addToCart(product) },
            onQuantityChange = { product, quantity ->
                homeViewModel.updateQuantity(product, quantity)
            }
        )

        binding.productsRecyclerView.apply {
            layoutManager = GridLayoutManager(this@HomeActivity, 2)
            adapter = productAdapter
        }

        // Will update when data changes
        homeViewModel.products.observe(this) { products ->
            productAdapter.submitList(products)
        }

        binding.cartFab.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                homeViewModel.onSearchQueryChanged(newText ?: "")
                return true
            }
        })
    }

    private fun observeData() {
        homeViewModel.cartCount.observe(this) { count ->
            if (count > 0) {
                binding.cartFab.show()
                binding.cartFab.showBadge()
            } else {
                binding.cartFab.hide()
            }
        }

        homeViewModel.categories.observe(this) { categories ->
            val allCategories = listOf("All") + categories
            categoryAdapter = CategoryAdapter(allCategories) { category ->
                homeViewModel.onCategorySelected(category)
            }
            binding.categoriesRecyclerView.adapter = categoryAdapter
        }
    }
}