package com.example.liboffan.model

import com.google.gson.annotations.SerializedName

data class UserLibraryItem(
    val treeId: Long,
    val title: String,
    @SerializedName("versionId")
    val versionId: Long? = null,
    @SerializedName("synopsis")
    val synopsis: String? = null,
    @SerializedName("versionSynopsis")
    val versionSynopsis: String? = null,
    val tags: List<String> = emptyList(),
    val fandoms: List<String> = emptyList(),
    val ageRating: String? = null,

    @SerializedName("authorDisplayName")
    val author: String,
    val status: String, // "reading", "completed", "planned", "dropped"
    val lastReadVersionId: Long?,
    val coverUrl: String?
) {
    val effectiveSynopsis: String?
        get() = versionSynopsis?.takeIf { it.isNotBlank() } ?: synopsis
}