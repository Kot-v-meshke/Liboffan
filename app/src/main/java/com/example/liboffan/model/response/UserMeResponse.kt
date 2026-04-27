package com.example.liboffan.model

data class UserFullResponse(
    val profile: UserProfile,
    val library: List<UserLibraryItem>
)