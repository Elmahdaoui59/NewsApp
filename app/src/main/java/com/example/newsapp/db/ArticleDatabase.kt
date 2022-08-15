package com.example.newsapp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.newsapp.models.Article

@Database(entities = [Article::class, RemoteKeys::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class ArticleDatabase: RoomDatabase() {
    abstract fun articleDao(): ArticleDao
    abstract fun remoteKeysDao(): RemoteKeysDao

    companion object {
        @Volatile
        private var instance: ArticleDatabase? = null


        fun getInstance(context: Context): ArticleDatabase =
            instance ?: synchronized(this) {
                instance
                    ?: buildDatabase(context).also { instance = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext,
                ArticleDatabase::class.java, "Article database")
                .build()
    }
}