package com.hustlers.grocekit.data.repository

import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private var storedVerificationId: String? = null

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    fun sendOtp(
        phoneNumber: String,
        callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    ) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(null)
            .setCallbacks(callbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun onCodeSent(verificationId: String) {
        storedVerificationId = verificationId
    }

    suspend fun verifyOtp(otp: String): Boolean {
        val verificationId = storedVerificationId ?: return false
        val credential = PhoneAuthProvider.getCredential(verificationId, otp)

        return try {
            auth.signInWithCredential(credential).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    fun signOut() {
        auth.signOut()
    }
}