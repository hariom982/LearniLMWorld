package com.example.learnilmworld.models

data class User(
    val uid: String = "",
    val email: String = "",
    val fullName: String = "",
    val lastName: String = "",
    val phoneNumber: String = "",
    val userType: String = "", // "STUDENT" or "TRAINER"
    val createdAt: Long = System.currentTimeMillis(),

    // Student specific fields
    val nativeLanguage: String = "",
    val languagesToLearn: List<String> = emptyList(),
    val learningLevel: String = "",
    val location: String = "",
    val qualification: String = "",
    val college: String = "",

    // Trainer specific fields
    val bio: String = "",
    val yearsOfExperience: Int = 0,
    val hourlyRate: Double = 0.0,
    val teachingStyle: String = "",
    val languagesToTeach: List<String> = emptyList(),
    val specializations: List<String> = emptyList(),
    val certification: String = "",
    val nationality: String = "",
    val isAvailableForBookings: Boolean = true,
    val averageRating: Double = 0.0,
    val profileImageUrl: String = ""
)
