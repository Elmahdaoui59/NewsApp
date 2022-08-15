package com.example.newsapp.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RemoteKeysDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKeys: List<RemoteKeys>)

    @Query("SELECT * FROM remote_keys WHERE articleId = :articleId")
    suspend fun remoteKeysArticleId(articleId: Long): RemoteKeys?

    @Query("DELETE FROM remote_keys WHERE NOT breaking")
    suspend fun clearRemoteKeys()

    @Query("DELETE FROM remote_keys WHERE breaking")
    suspend fun clearBreakRemoteKeys()
}


