package com.example.liboffan.network

import com.example.liboffan.model.LoginRequest
import com.example.liboffan.model.RegisterRequest
import com.example.liboffan.model.AuthResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface AuthService {
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>
}

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:8080/" // ← замени на свой IP при необходимости

    val instance: AuthService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthService::class.java)
    }
}