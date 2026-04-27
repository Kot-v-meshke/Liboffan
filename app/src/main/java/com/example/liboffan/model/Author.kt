package com.example.liboffan.model

import com.google.gson.annotations.SerializedName
data class Author(
    val id: Long,
    val displayName: String?,
    @SerializedName("avatarUrl")
    val avatarUrl: String? = null,
    val email: String? = null,
    @SerializedName("createdAt")
    val createdAt: String? = null
)