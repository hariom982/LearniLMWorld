package com.example.learnilmworld.retrofit

data class TokenResponse(
    val token: String
)
// retrofit/RoomResponse.kt
data class RoomResponse(
    val roomId: String,
    val roomName: String
)