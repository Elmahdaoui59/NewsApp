package com.example.newsapp.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "articles")
data class Article(
    @PrimaryKey(autoGenerate = true)
    @field:SerializedName("id")  val id: Long,
    @field:SerializedName("content") val content: String?,
    @field:SerializedName("description") val description: String?,
    @field:SerializedName("publishedAt") val publishedAt: String?,
    @field:SerializedName("source") var source: Source?,
    @field:SerializedName("title") val title: String,
    @field:SerializedName("url") val url: String,
    @field:SerializedName("urlToImage") val urlToImage: String?,
    var favorite: Boolean = false,
    var breaking: Boolean = false
): java.io.Serializable

