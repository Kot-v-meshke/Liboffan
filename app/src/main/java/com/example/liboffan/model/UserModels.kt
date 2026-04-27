package com.example.liboffan.model

import com.google.gson.annotations.SerializedName

data class UserProfile(
    val id: Long,
    val email: String,
    val displayName: String?,
    val avatarUrl: String? = null,
    val storyCount: Int = 0
)


