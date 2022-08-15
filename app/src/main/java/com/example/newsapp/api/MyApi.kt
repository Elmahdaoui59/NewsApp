package com.example.newsapp.api

import com.example.newsapp.models.NewsResponse
import com.example.newsapp.util.Constants
import com.example.newsapp.util.Constants.Companion.API_KEY
import retrofit2.http.GET
import retrofit2.http.Query

interface MyApi {

    @GET("top-headlines")
    suspend fun getArticles(
        @Query("country") country: String = "us",
        @Query("page") pageNumber: Int,
        @Query("pageSize") pageSize: Int = Constants.pageSize,
        @Query("apiKey") apiKey: String = API_KEY
    ): NewsResponse

    @GET("everything")
    suspend fun searchForNews(
        @Query("q") searchQuery:String,
        @Query("page") pageNumber: Int,
        @Query("pageSize") pageSize: Int,
        @Query("apiKey") apiKey: String = API_KEY
    ):NewsResponse
}