package com.example.liboffan.model

import com.google.gson.annotations.SerializedName

data class StoryItem(
    val id: Long,
    val title: String,
    val author: Author,
    @SerializedName("CoverUrl") // заглавные буквы!
    val coverUrl: String? = null,
    @SerializedName("ageRating")
    val ageRating: String? = null,
    val tags: List<String> = emptyList(),
    val fandoms: List<String> = emptyList(), // ← добавь это поле!

    @SerializedName("branches")
    val branches: List<BranchItem> = emptyList(),

    @SerializedName("hasMoreBranches")
    val hasMoreBranches: Boolean = false
)

data class BranchItem(
    @SerializedName("versionId")
    val versionId: Long,
    val title: String,
    val authorName: String,
    val createdAt: String,  // или LocalDateTime с адаптером
    val tags: List<String> = emptyList(),
    val ageRating: String? = null
)