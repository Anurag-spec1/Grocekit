package com.hustlers.grocekit.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.hustlers.grocekit.R
import com.hustlers.grocekit.databinding.ActivityOrderSuccessBinding

class OrderSuccessActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderSuccessBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_success)

        // Get data from intent
        val orderId = intent.getStringExtra("orderId") ?: "#ORD000"
        val totalAmount = intent.getDoubleExtra("totalAmount", 0.0)
        val estimatedDelivery = intent.getStringExtra("estimatedDelivery") ?: "30-45 mins"
        val paymentMethod = intent.getStringExtra("paymentMethod") ?: "COD"
        val itemCount = intent.getStringExtra("itemCount") ?: "0"

        // Set data to views
        binding.apply {
            orderIdText.text = orderId
            totalAmountText.text = "₹${String.format("%.2f", totalAmount)}"
            paymentMethodText.text = when (paymentMethod) {
                "ONLINE" -> "Online Payment"
                else -> "Cash on Delivery"
            }
            itemCountText.text = "$itemCount items"
            estimatedDeliveryText.text = "Estimated delivery in $estimatedDelivery"

            // Continue Shopping Button
            continueShoppingButton.setOnClickListener {
                val intent = Intent(this@OrderSuccessActivity, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }

            // View Orders Button (optional - you can implement if you have orders screen)
            viewOrdersButton.setOnClickListener {
                // Navigate to orders screen or home
                val intent = Intent(this@OrderSuccessActivity, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
        }
    }

    override fun onBackPressed() {
        // Prevent going back to checkout
        super.onBackPressed()
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}