package com.example.liboffan.model.dto

import com.example.liboffan.model.Author
import com.google.gson.annotations.SerializedName

data class StoryDetailDto(
    val treeId: Long,
    val title: String,
    val synopsis: String? = null,
    val versionSynopsis: String? = null,
    val content: String,
    @SerializedName("coverUrl")
    val coverUrl: String? = null,
    val author: Author,
    val tags: List<String> = emptyList(),
    val fandoms: List<String> = emptyList(),
    @SerializedName("ageRating")
    val ageRating: String = "G",
    val likeCount: Int = 0,
    val versionId: Long,
    val forkCount: Int = 0,
    val published: Boolean = true,
    val isLikedByCurrentUser: Boolean = false,
    @SerializedName("versionCreatedAt")
    val versionCreatedAt: String? = null,
    @SerializedName("currentLibraryStatus")
    val currentLibraryStatus: String? = null
) {
    val effectiveSynopsis: String?
        get() = versionSynopsis?.takeIf { it.isNotBlank() } ?: synopsis
}