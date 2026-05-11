package com.hustlers.grocekit.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hustlers.grocekit.data.model.CartItem
import com.hustlers.grocekit.data.model.Order
import com.hustlers.grocekit.data.repository.AuthRepository
import com.hustlers.grocekit.data.repository.OrderRepository
import kotlinx.coroutines.launch

class CheckoutViewModel : ViewModel() {
    private val authRepository = AuthRepository()
    private val orderRepository = OrderRepository()

    private val _name = MutableLiveData<String>()
    val name: LiveData<String> = _name

    private val _phone = MutableLiveData<String>()
    val phone: LiveData<String> = _phone

    private val _address = MutableLiveData<String>()
    val address: LiveData<String> = _address

    private val _paymentMethod = MutableLiveData<String>("COD")
    val paymentMethod: LiveData<String> = _paymentMethod

    private val _totalAmount = MutableLiveData<Double>()
    val totalAmount: LiveData<Double> = _totalAmount

    private val _orderSuccess = MutableLiveData<Order>()
    val orderSuccess: LiveData<Order> = _orderSuccess

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private var cartItems: List<CartItem> = emptyList()

    fun setCartData(items: List<CartItem>, total: Double) {
        this.cartItems = items
        _totalAmount.value = total
    }

    fun onNameChanged(name: String) {
        _name.value = name
    }

    fun onPhoneChanged(phone: String) {
        _phone.value = phone
    }

    fun onAddressChanged(address: String) {
        _address.value = address
    }

    fun onPaymentMethodChanged(method: String) {
        _paymentMethod.value = method
    }

    fun placeOrder() {
        viewModelScope.launch {
            val userId = authRepository.getCurrentUserId() ?: return@launch

            val order = Order(
                orderId = "",
                userId = userId,
                userName = _name.value ?: "",
                phone = _phone.value ?: "",
                address = _address.value ?: "",
                totalAmount = _totalAmount.value ?: 0.0,
                paymentMethod = _paymentMethod.value ?: "COD",
                items = cartItems
            )

            try {
                val orderId = orderRepository.saveOrder(order)
                val savedOrder = order.copy(orderId = orderId)
                _orderSuccess.value = savedOrder
            } catch (e: Exception) {
                _error.value = "Failed to place order: ${e.message}"
            }
        }
    }
}