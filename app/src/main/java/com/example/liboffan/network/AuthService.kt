package com.example.liboffan.network

import com.example.liboffan.model.LoginRequest
import com.example.liboffan.model.RegisterRequest
import com.example.liboffan.model.response.AuthResponse
import com.example.liboffan.model.UserFullResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Header

interface AuthService {
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>
    @GET("/api/users/me/full")
    suspend fun getUserFull(@Header("Authorization") authHeader: String): Response<UserFullResponse>
}