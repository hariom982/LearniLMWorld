package com.example.learnilmworld.retrofit

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface TokenApi {

    @POST("token/generate-token")  // Your endpoint path
    fun getToken(@Body request: Map<String, String>): Call<TokenResponse>
}