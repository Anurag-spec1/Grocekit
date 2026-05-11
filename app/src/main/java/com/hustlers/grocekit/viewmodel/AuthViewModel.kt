package com.hustlers.grocekit.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.hustlers.grocekit.data.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val authRepository = AuthRepository()

    private val _phoneNumber = MutableLiveData<String>()
    val phoneNumber: LiveData<String> = _phoneNumber

    private val _otp = MutableLiveData<String>()
    val otp: LiveData<String> = _otp

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _otpSent = MutableLiveData<Boolean>()
    val otpSent: LiveData<Boolean> = _otpSent

    private val _loginSuccess = MutableLiveData<Boolean>()
    val loginSuccess: LiveData<Boolean> = _loginSuccess

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun onPhoneNumberChanged(number: String) {
        _phoneNumber.value = number
    }

    fun onOtpChanged(otp: String) {
        _otp.value = otp
    }

    fun sendOtp() {
        _isLoading.value = true
        val phone = _phoneNumber.value ?: return

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                _isLoading.postValue(false)
                _loginSuccess.postValue(true)
            }

            override fun onVerificationFailed(e: com.google.firebase.FirebaseException) {
                _isLoading.postValue(false)
                _error.postValue("Verification failed: ${e.message}")
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                authRepository.onCodeSent(verificationId)
                _isLoading.postValue(false)
                _otpSent.postValue(true)
            }

        }

        authRepository.sendOtp(phone, callbacks)
    }

    fun verifyOtp() {
        viewModelScope.launch {
            _isLoading.value = true
            val otp = _otp.value ?: return@launch

            if (authRepository.verifyOtp(otp)) {
                _loginSuccess.value = true
            } else {
                _error.value = "Invalid OTP"
            }
            _isLoading.value = false
        }
    }

    fun isLoggedIn(): Boolean = authRepository.isUserLoggedIn()
}