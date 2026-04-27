package com.example.liboffan.model

data class RegisterRequest(
    val email: String,
    val password: String,
    val displayName: String
)