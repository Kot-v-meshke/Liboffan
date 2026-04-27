package com.example.liboffan.model

data class SearchResponse(
    val content: List<StoryItem>,
    val totalElements: Int
)

