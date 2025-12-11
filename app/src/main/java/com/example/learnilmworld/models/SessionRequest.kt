package com.example.learnilmworld.models

import com.google.firebase.firestore.DocumentId
import java.util.Date

/**
 * Session Request Model
 * This represents a booking request from a student to a trainer
 *
 * Firestore Collection: "sessionRequests"
 */
data class SessionRequest(
    @DocumentId
    val id: String = "",

    // User Information
    val studentId: String = "",
    val trainerId: String = "",
    val studentName: String = "",
    val trainerName: String = "",

    // Session Details
    val date: Date? = null,
    val timeSlot: String = "",           // e.g., "09:00 AM"
    val duration: Int = 0,               // in minutes
    val description: String = "",
    val language: String = "",
    val level: String = "",              // beginner, intermediate, advanced

    // Status and Payment
    val status: String = "pending",      // pending, confirmed, rejected, completed
    val totalPrice: Double = 0.0,
    val roomId: String = "",             // Generated when confirmed

    // Timestamps
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
)