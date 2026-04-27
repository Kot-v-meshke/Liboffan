package com.example.liboffan.model

import com.google.gson.annotations.SerializedName

data class StoryResponse(
    val content: List<StoryItem>,
    val totalElements: Int
)


