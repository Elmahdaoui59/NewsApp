package com.example.newsapp.models

import com.example.newsapp.models.Article

data class NewsResponse(
    val articles: List<Article>,
    val status: String,
    val totalResults: Int
)