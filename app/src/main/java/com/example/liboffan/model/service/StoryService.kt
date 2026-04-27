package com.example.liboffan.model.service

import com.example.liboffan.model.FandomItem
import com.example.liboffan.model.SearchResponse
import com.example.liboffan.model.StoryResponse
import com.example.liboffan.model.TagItem
import com.example.liboffan.model.dto.StoryDetailDto
import com.example.liboffan.model.request.CreateStoryRequest
import com.example.liboffan.model.request.LibraryUpdateRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface StoryService {
    @GET("/api/stories/latest")
    suspend fun getLatestStories(): Response<StoryResponse>

    @GET("/api/stories/search")
    suspend fun searchStories(
        @Query("query") query: String? = null,
        @Query("fandom") fandom: String? = null,
        @Query("tag") tags: List<String>?,
        @Query("ageRating") ageRating: String? = null
    ): Response<SearchResponse>

    @GET("/api/stories/{treeId}")
    suspend fun getStoryDetail(
        @Path("treeId") treeId: Long,
        @Header("Authorization") authHeader: String
    ): Response<StoryDetailDto>

    @POST("/api/stories/versions/{versionId}/like")
    suspend fun toggleLike(
        @Path("versionId") versionId: Long,
        @Header("Authorization") authHeader: String
    ): Response<Unit>

    @GET("/api/stories/versions/{versionId}")
    suspend fun getStoryVersion(
        @Path("versionId") versionId: Long,
        @Header("Authorization") authHeader: String
    ): Response<StoryDetailDto>

    @GET("/api/tags")
    suspend fun getAllTags(): Response<List<TagItem>>

    @GET("/api/fandoms")
    suspend fun getAllFandoms(): Response<List<FandomItem>>

    @POST("/api/me/library")
    suspend fun updateLibraryStatus(
        @Header("Authorization") authHeader: String,
        @Body request: LibraryUpdateRequest
    ): Response<Unit>

    @DELETE("/api/me/library/{versionId}")
    suspend fun removeBookFromLibrary(
        @Header("Authorization") authHeader: String,
        @Path("versionId") versionId: Long
    ): Response<Unit>

    @POST("/api/stories")
    suspend fun createStory(
        @Header("Authorization") authHeader: String,
        @Body request: CreateStoryRequest
    ): Response<StoryDetailDto>
}