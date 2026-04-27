package com.example.liboffan.model.request

data class CreateStoryRequest(
    val title: String,
    val synopsis: String? = null,
    val content: String,
    val ageRating: String,
    val tags: List<String> = emptyList(),
    val fandoms: List<String> = emptyList()
)