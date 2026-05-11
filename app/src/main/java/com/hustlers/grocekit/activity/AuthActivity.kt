package com.hustlers.grocekit.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.hustlers.grocekit.R
import com.hustlers.grocekit.databinding.ActivityAuthBinding
import com.hustlers.grocekit.viewmodel.AuthViewModel

class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_auth)
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        binding.viewModel = authViewModel
        binding.lifecycleOwner = this

        observeViewModel()
    }

    private fun observeViewModel() {
        authViewModel.loginSuccess.observe(this) { success ->
            if (success) {
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }
        }

        authViewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
    }
}