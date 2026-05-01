package com.example.liboffan.model.request

data class ForkStoryRequest(
    val content: String,
    val versionSynopsis: String? = null,
    val ageRating: String,
    val tags: List<String> = emptyList(),
    val parentVersionId: Long
)