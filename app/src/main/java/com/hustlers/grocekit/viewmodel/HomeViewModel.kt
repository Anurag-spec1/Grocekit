package com.hustlers.grocekit.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hustlers.grocekit.data.model.CartItem
import com.hustlers.grocekit.data.model.Product
import com.hustlers.grocekit.data.repository.ProductRepository
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val productRepository = ProductRepository()

    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> = _products

    private val _filteredProducts = MutableLiveData<List<Product>>()
    val filteredProducts: LiveData<List<Product>> = _filteredProducts

    private val _cartItems = MutableLiveData<MutableList<CartItem>>(mutableListOf())
    val cartItems: LiveData<MutableList<CartItem>> = _cartItems

    private val _categories = MutableLiveData<List<String>>()
    val categories: LiveData<List<String>> = _categories

    private val _selectedCategory = MutableLiveData<String>()
    val selectedCategory: LiveData<String> = _selectedCategory

    private val _searchQuery = MutableLiveData<String>()
    val searchQuery: LiveData<String> = _searchQuery

    private val _cartCount = MutableLiveData<Int>()
    val cartCount: LiveData<Int> = _cartCount

    init {
        loadProducts()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            productRepository.getProducts().collect { productList ->
                _products.value = productList
                _filteredProducts.value = productList

                val uniqueCategories = productList.map { it.category }.distinct()
                _categories.value = uniqueCategories
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        filterProducts()
    }

    fun onCategorySelected(category: String) {
        _selectedCategory.value = category
        filterProducts()
    }

    private fun filterProducts() {
        val allProducts = _products.value ?: return
        var filtered = allProducts

        val query = _searchQuery.value
        if (!query.isNullOrEmpty()) {
            filtered = filtered.filter {
                it.name.contains(query, ignoreCase = true)
            }
        }

        val category = _selectedCategory.value
        if (!category.isNullOrEmpty() && category != "All") {
            filtered = filtered.filter { it.category == category }
        }

        _filteredProducts.value = filtered
    }

    fun addToCart(product: Product) {
        val currentCart = _cartItems.value ?: mutableListOf()
        val existingItem = currentCart.find { it.product.id == product.id }

        if (existingItem != null) {
            existingItem.quantity++
        } else {
            currentCart.add(CartItem(product))
        }

        _cartItems.value = currentCart
        updateCartCount()
    }

    fun removeFromCart(product: Product) {
        val currentCart = _cartItems.value ?: mutableListOf()
        currentCart.removeAll { it.product.id == product.id }
        _cartItems.value = currentCart
        updateCartCount()
    }

    fun updateQuantity(product: Product, quantity: Int) {
        val currentCart = _cartItems.value ?: mutableListOf()
        val item = currentCart.find { it.product.id == product.id }

        if (item != null) {
            if (quantity > 0) {
                item.quantity = quantity
            } else {
                currentCart.remove(item)
            }
        }

        _cartItems.value = currentCart
        updateCartCount()
    }

    private fun updateCartCount() {
        val count = _cartItems.value?.sumOf { it.quantity } ?: 0
        _cartCount.value = count
    }

    fun getCartTotal(): Double {
        return _cartItems.value?.sumOf { it.totalPrice } ?: 0.0
    }
}