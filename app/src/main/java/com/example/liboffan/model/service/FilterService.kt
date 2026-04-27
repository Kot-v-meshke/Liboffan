package com.example.liboffan.model.service

import com.example.liboffan.model.FandomItem
import com.example.liboffan.model.TagItem
import com.example.liboffan.network.RetrofitClient

object FilterService {

    suspend fun loadTags(): List<TagItem> {
        return try {
            val response = RetrofitClient.storyService.getAllTags()
            if (response.isSuccessful && response.body() != null) {
                response.body()!!
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun loadFandoms(): List<FandomItem> {
        return try {
            val response = RetrofitClient.storyService.getAllFandoms()
            if (response.isSuccessful && response.body() != null) {
                response.body()!!
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}