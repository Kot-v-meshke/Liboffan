package com.example.liboffan.model

data class AuthResponse(
    val token: String,
    val email: String,
    val displayName: String?
)