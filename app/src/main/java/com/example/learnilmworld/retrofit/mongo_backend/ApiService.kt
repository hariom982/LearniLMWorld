package com.example.learnilmworld.retrofit.mongo_backend

// src/main/kotlin/api/ApiService.kt
import com.example.learnilmworld.models.User
import com.example.learnilmworld.retrofit.RoomResponse
import com.example.learnilmworld.retrofit.TokenResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Body

interface ApiService {
    @GET("profile/me")
    suspend fun getProfile(
        @Header("Authorization") token: String
    ): User

    @POST("profile")
    suspend fun saveProfile(
        @Header("Authorization") token: String,
        @Body profile: User
    ): User

}

data class ProfileResponse(
    val userId: String? = null,
    val email: String = "",
    val fullName: String = "",
    val lastName: String = "",
    val phoneNumber: String = "",
    val userType: String = "",
    val bio: String? = null,
    val yearsOfExperience: Int? = null,
    val hourlyRate: Double? = null,
    val teachingStyle: String? = null,
    val languagesToTeach: List<String>? = null,
    val languagesToLearn: List<String>? = null,
    val specializations: List<String>? = null,
    val certification: String? = null,
    val nationality: String? = null,
    val location: String? = null,
    val qualification: String? = null,
    val college: String? = null,
    val isAvailableForBookings: Boolean? = null,
    val averageRating: Double? = null,
    val createdAt: Long? = null,
    val updatedAt: Long? = null
)

data class ProfileRequest(
    val name: String,
)