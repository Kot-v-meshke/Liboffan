package com.example.liboffan.model

import com.google.gson.annotations.SerializedName

data class FandomItem(
    val id: Long,
    val name: String,              //Harry Potter для запросов
    @SerializedName("displayName")
    val displayName: String,       //Гарри Поттер для отображения
    val description: String? = null
)