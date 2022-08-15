package com.example.newsapp.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKeys(
    @PrimaryKey(autoGenerate = true)
    val articleId: Long,
    val prevKey: Int?,
    val nextKey: Int?,
    var breaking: Boolean = false
)

