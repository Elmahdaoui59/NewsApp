package com.example.newsapp.api

import com.example.newsapp.models.NewsResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "https://newsapi.org/v2/"

    private val retrofit by lazy {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }
    private val newsApi: MyApi by lazy {
        retrofit.create(MyApi::class.java)
    }

    suspend fun getNews(page: Int): NewsResponse {
        return newsApi.getArticles(pageNumber = page)
    }

    suspend fun searchNews(searchQuery: String, page:Int, pageSize: Int): NewsResponse {
        return newsApi.searchForNews(searchQuery,page, pageSize)
    }
}