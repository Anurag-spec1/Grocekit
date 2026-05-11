package com.hustlers.grocekit.activity


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.hustlers.grocekit.R
import com.hustlers.grocekit.databinding.ActivityCheckoutBinding
import com.hustlers.grocekit.viewmodel.CheckoutViewModel
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONObject

class CheckoutActivity : AppCompatActivity(), PaymentResultListener {
    private lateinit var binding: ActivityCheckoutBinding
    private lateinit var checkoutViewModel: CheckoutViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_checkout)
        checkoutViewModel = ViewModelProvider(this)[CheckoutViewModel::class.java]

        binding.viewModel = checkoutViewModel
        binding.lifecycleOwner = this

        // Get total amount from intent
        val totalAmount = intent.getDoubleExtra("totalAmount", 0.0)
        checkoutViewModel.setCartData(
            emptyList(),
            totalAmount
        )

        setupClickListeners()
        observeViewModel()
    }

    private fun setupClickListeners() {
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        // Place order button
        binding.placeOrderButton.setOnClickListener {
            if (validateInputs()) {
                when (checkoutViewModel.paymentMethod.value) {
                    "ONLINE" -> startRazorpayPayment()
                    else -> checkoutViewModel.placeOrder()
                }
            }
        }
    }

    private fun validateInputs(): Boolean {
        val name = checkoutViewModel.name.value
        val phone = checkoutViewModel.phone.value
        val address = checkoutViewModel.address.value

        when {
            name.isNullOrEmpty() -> {
                binding.nameLayout.error = "Name is required"
                return false
            }
            phone.isNullOrEmpty() -> {
                binding.phoneLayout.error = "Phone number is required"
                return false
            }
            phone.length < 10 -> {
                binding.phoneLayout.error = "Enter valid phone number"
                return false
            }
            address.isNullOrEmpty() -> {
                binding.addressLayout.error = "Address is required"
                return false
            }
            else -> {
                binding.nameLayout.error = null
                binding.phoneLayout.error = null
                binding.addressLayout.error = null
                return true
            }
        }
    }

    private fun startRazorpayPayment() {
        val checkout = Checkout()
        checkout.setKeyID("rzp_test_YOUR_KEY_ID")

        val options = JSONObject().apply {
            put("name", "Mini Grocery")
            put("description", "Grocery Order Payment")
            put("image", "https://your-app-icon-url.png")
            put("currency", "INR")
            put("amount", ((checkoutViewModel.totalAmount.value ?: 0.0) * 100).toInt())

            put("prefill", JSONObject().apply {
                put("contact", checkoutViewModel.phone.value ?: "")
            })

            put("theme", JSONObject().apply {
                put("color", "#4CAF50")
            })
        }

        try {
            checkout.open(this, options)
        } catch (e: Exception) {
            Toast.makeText(this, "Payment Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPaymentSuccess(razorpayPaymentId: String?) {
        Toast.makeText(this, "Payment Successful", Toast.LENGTH_SHORT).show()
        checkoutViewModel.placeOrder()
    }

    override fun onPaymentError(code: Int, response: String?) {
        Toast.makeText(this, "Payment Failed: $response", Toast.LENGTH_SHORT).show()
    }

    private fun observeViewModel() {
        checkoutViewModel.orderSuccess.observe(this) { order ->
            if (order != null) {
                val intent = Intent(this, OrderSuccessActivity::class.java).apply {
                    putExtra("orderId", order.orderId)
                    putExtra("totalAmount", order.totalAmount)
                    putExtra("estimatedDelivery", order.estimatedDelivery)
                    putExtra("paymentMethod", order.paymentMethod)
                    putExtra("itemCount", order.items.size.toString())
                }
                startActivity(intent)
                finish()
            }
        }

        checkoutViewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.GONE
            }
        }
    }
}