package com.hustlers.grocekit.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hustlers.grocekit.data.model.CartItem

class CartViewModel : ViewModel() {
    private val _cartItems = MutableLiveData<List<CartItem>>()
    val cartItems: LiveData<List<CartItem>> = _cartItems

    private val _subtotal = MutableLiveData<Double>()
    val subtotal: LiveData<Double> = _subtotal

    private val _deliveryFee = MutableLiveData(20.0)
    val deliveryFee: LiveData<Double> = _deliveryFee

    private val _totalAmount = MutableLiveData<Double>()
    val totalAmount: LiveData<Double> = _totalAmount

    fun setCartItems(items: List<CartItem>) {
        _cartItems.value = items
        calculateTotals()
    }

    fun updateQuantity(item: CartItem, newQuantity: Int) {
        if (newQuantity > 0) {
            val updatedItems = _cartItems.value?.map {
                if (it.product.id == item.product.id) it.copy(quantity = newQuantity)
                else it
            }
            _cartItems.value = updatedItems!!
        } else {
            removeItem(item)
        }
        calculateTotals()
    }

    fun removeItem(item: CartItem) {
        _cartItems.value = _cartItems.value?.filter { it.product.id != item.product.id }
        calculateTotals()
    }

    private fun calculateTotals() {
        val subTotal = _cartItems.value?.sumOf { it.totalPrice } ?: 0.0
        _subtotal.value = subTotal
        _totalAmount.value = subTotal + (_deliveryFee.value ?: 0.0)
    }
}