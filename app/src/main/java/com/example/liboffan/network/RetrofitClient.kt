package com.example.liboffan.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import com.example.liboffan.model.service.StoryService
import com.example.liboffan.network.ApiConfig.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitClient {

    val instance: AuthService by lazy {
        Retrofit.Builder()
            .baseUrl(ApiConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthService::class.java)
    }

    val storyService: StoryService by lazy {
        retrofit2.Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
            .build()
            .create(StoryService::class.java)
    }
}