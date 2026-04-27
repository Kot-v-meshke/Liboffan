package com.example.liboffan.model.request

import com.google.gson.annotations.SerializedName

data class LibraryUpdateRequest(
    @SerializedName("treeId")
    val treeId: Long,

    @SerializedName("status")
    val status: String,  // "planned","reading", "completed","dropped"
    val versionId: Long? = null
)