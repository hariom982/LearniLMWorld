package com.example.learnilmworld.retrofit.mongo_backend

import android.util.Log
import com.example.learnilmworld.models.User
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

object AuthManager {
    private val firebaseAuth: FirebaseAuth = Firebase.auth

    // Sign up (Firebase only)
    suspend fun signUp(email: String, password: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            // Create user
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()

            // Get the current user
            val user = authResult.user ?: return@withContext Result.failure(Exception("User creation failed"))

            // Fetch ID token (important!)
            val token = user.getIdToken(false).await().token
                ?: return@withContext Result.failure(Exception("Failed to get token after signup"))

            Result.success(token)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Login (Firebase only)
    suspend fun login(email: String, password: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val token = result.user?.getIdToken(false)?.await()?.token
                ?: return@withContext Result.failure(Exception("Failed to get token"))
            Result.success(token)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getProfile(token: String): Result<User> = withContext(Dispatchers.IO) {
        try {
            val profile = ApiClient.apiService.getProfile("Bearer $token")
            Result.success(profile)
        } catch (e: Exception) {
            Log.e("Auth Massenger","Failed to fetch profile from mongo",e)
            Result.failure(e)
        }
    }

    suspend fun saveProfile(token: String, user: User): Result<User> = withContext(Dispatchers.IO) {
        try {
            val profile = ApiClient.apiService.saveProfile(
                "Bearer $token",
                user
            )
            Result.success(profile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}