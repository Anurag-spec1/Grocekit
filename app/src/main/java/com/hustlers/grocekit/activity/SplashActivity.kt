package com.hustlers.grocekit.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.hustlers.grocekit.R
import com.hustlers.grocekit.databinding.ActivitySplashBinding
import com.hustlers.grocekit.viewmodel.AuthViewModel

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private val authViewModel = AuthViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
        binding.viewModel = authViewModel
        binding.lifecycleOwner = this

        Handler(Looper.getMainLooper()).postDelayed({
            if (authViewModel.isLoggedIn()) {
                startActivity(Intent(this, HomeActivity::class.java))
            } else {
                startActivity(Intent(this, AuthActivity::class.java))
            }
            finish()
        }, 2000)
    }
}