package com.example.learnilmworld.retrofit.mongo_backend

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
//    private const val BASE_URL = "http://192.168.29.124:5000/api/" // e.g. https://myapp-backend.onrender.com/api/
//    private const val BASE_URL = "https://learnilmworld-backend-4.onrender.com/api/" // e.g. https://myapp-backend.onrender.com/api/
    private const val BASE_URL = "https://learnilmworld-backend-4.onrender.com/api/" // e.g. https://myapp-backend.onrender.com/api/

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(OkHttpClient.Builder().build())
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)
}