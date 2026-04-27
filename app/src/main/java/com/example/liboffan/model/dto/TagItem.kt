package com.example.liboffan.model

import com.google.gson.annotations.SerializedName

data class TagItem(
    val id: Long,
    val name: String,              // fantasy для запросов
    @SerializedName("displayName")
    val displayName: String        // фэнтези для отображения
)