package com.hustlers.grocekit.data.repository

import com.google.firebase.database.FirebaseDatabase
import com.hustlers.grocekit.data.model.Order

class OrderRepository {
    private val database = FirebaseDatabase.getInstance().getReference("orders")

    suspend fun saveOrder(order: Order): String {
        val orderId = database.push().key ?: ""
        val orderWithId = order.copy(orderId = orderId)
        database.child(orderId).setValue(orderWithId)
        return orderId
    }
}