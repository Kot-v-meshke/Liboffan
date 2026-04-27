package com.example.liboffan.model.response

data class AuthResponse(
    val token: String,
    val email: String,
    val displayName: String?
)