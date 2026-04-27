package com.example.liboffan.model.service

import com.example.liboffan.model.UserFullResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers

interface AuthService {
    @GET("/api/users/me/full")
    @Headers("Content-Type: application/json")
    suspend fun getUserFull(@Header("Authorization") authHeader: String): Response<UserFullResponse>
}