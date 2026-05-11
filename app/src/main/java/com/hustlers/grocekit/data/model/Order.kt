package com.hustlers.grocekit.data.model

data class Order(
    val orderId: String = "",
    val userId: String = "",
    val userName: String = "",
    val phone: String = "",
    val address: String = "",
    val totalAmount: Double = 0.0,
    val paymentMethod: String = "",
    val paymentStatus: String = "PENDING",
    val items: List<CartItem> = emptyList(),
    val timestamp: Long = System.currentTimeMillis(),
    val estimatedDelivery: String = "30-45 minutes"
)
