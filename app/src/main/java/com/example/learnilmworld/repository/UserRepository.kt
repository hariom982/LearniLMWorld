package com.example.learnilmworld.repository

import android.util.Log
import com.example.learnilmworld.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("users")

    // Get current Firebase user
    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    // Check if user is logged in
    fun isUserLoggedIn(): Boolean = auth.currentUser != null

    // Register Student
    suspend fun registerStudent(
        email: String,
        password: String,
        fullName: String,
        lastName: String,
        phoneNumber: String,
        nativeLanguage: String,
        languagesToLearn: List<String>,
        learningLevel: String,
        location: String = "",
        qualification: String = "",
        college: String = ""
    ): Result<User> = withContext(Dispatchers.IO) {
        try {
            // Create Firebase Authentication user
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user ?: throw Exception("User creation failed")

            // Create user data object
            val user = User(
                uid = firebaseUser.uid,
                email = email,
                fullName = fullName,
                lastName = lastName,
                phoneNumber = phoneNumber,
                userType = "STUDENT",
                nativeLanguage = nativeLanguage,
                languagesToLearn = languagesToLearn,
                learningLevel = learningLevel,
                location = location,
                qualification = qualification,
                college = college,
                createdAt = System.currentTimeMillis()
            )

            // Save user data to Firestore
            usersCollection.document(firebaseUser.uid).set(user).await()

            Log.d("FirebaseRepo", "Student registered: ${firebaseUser.uid}")
            Result.success(user)

        } catch (e: Exception) {
            Log.e("FirebaseRepo", "Student registration failed", e)
            Result.failure(e)
        }
    }

    // Register Trainer
    suspend fun registerTrainer(
        email: String,
        password: String,
        fullName: String,
        lastName: String,
        phoneNumber: String,
        bio: String,
        yearsOfExperience: Int,
        hourlyRate: Double,
        teachingStyle: String,
        languagesToTeach: List<String>,
        specializations: List<String>,
        certification: String,
        nationality: String = ""
    ): Result<User> = withContext(Dispatchers.IO) {
        try {
            // Create Firebase Authentication user
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user ?: throw Exception("User creation failed")

            // Create user data object
            val user = User(
                uid = firebaseUser.uid,
                email = email,
                fullName = fullName,
                lastName = lastName,
                phoneNumber = phoneNumber,
                userType = "TRAINER",
                bio = bio,
                yearsOfExperience = yearsOfExperience,
                hourlyRate = hourlyRate,
                teachingStyle = teachingStyle,
                languagesToTeach = languagesToTeach,
                specializations = specializations,
                certification = certification,
                nationality = nationality,
                isAvailableForBookings = true,
                averageRating = 0.0,
                createdAt = System.currentTimeMillis()
            )

            // Save user data to Firestore
            usersCollection.document(firebaseUser.uid).set(user).await()

            Log.d("FirebaseRepo", "Trainer registered: ${firebaseUser.uid}")
            Result.success(user)

        } catch (e: Exception) {
            Log.e("FirebaseRepo", "Trainer registration failed", e)
            Result.failure(e)
        }
    }

    // Login User
    suspend fun loginUser(
        email: String,
        password: String
    ): Result<User> = withContext(Dispatchers.IO) {
        try {
            // Sign in with Firebase Authentication
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user ?: throw Exception("Login failed")

            // Get user data from Firestore
            val userDoc = usersCollection.document(firebaseUser.uid).get().await()

            if (!userDoc.exists()) {
                throw Exception("User data not found")
            }

            // Convert Firestore document to User object
            val user = userDoc.toObject(User::class.java) ?: throw Exception("Failed to parse user data")

            Log.d("FirebaseRepo", "User logged in: ${user.email}, Type: ${user.userType}")
            Result.success(user)

        } catch (e: Exception) {
            Log.e("FirebaseRepo", "Login failed", e)
            Result.failure(e)
        }
    }

    // Get User Data by UID
    suspend fun getUserData(uid: String): Result<User> = withContext(Dispatchers.IO) {
        try {
            val userDoc = usersCollection.document(uid).get().await()

            if (!userDoc.exists()) {
                throw Exception("User not found")
            }

            val user = userDoc.toObject(User::class.java) ?: throw Exception("Failed to parse user data")
            Result.success(user)

        } catch (e: Exception) {
            Log.e("FirebaseRepo", "Failed to get user data", e)
            Result.failure(e)
        }
    }

    // Get Current User Data
    suspend fun getCurrentUserData(): Result<User?> = withContext(Dispatchers.IO) {
        try {
            val currentUser = auth.currentUser
            if (currentUser == null) {
                return@withContext Result.success(null)
            }

            getUserData(currentUser.uid)

        } catch (e: Exception) {
            Log.e("FirebaseRepo", "Failed to get current user data", e)
            Result.failure(e)
        }
    }

    // Update User Data
    suspend fun updateUserData(uid: String, updates: Map<String, Any>): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            usersCollection.document(uid).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirebaseRepo", "Failed to update user data", e)
            Result.failure(e)
        }
    }

    // Logout User
    fun logoutUser() {
        auth.signOut()
        Log.d("FirebaseRepo", "User logged out")
    }

    // Reset Password
    suspend fun resetPassword(email: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirebaseRepo", "Password reset failed", e)
            Result.failure(e)
        }
    }
}