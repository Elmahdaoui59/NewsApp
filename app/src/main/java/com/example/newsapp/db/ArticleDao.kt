package com.example.newsapp.db

import androidx.paging.PagingSource
import androidx.room.*
import com.example.newsapp.models.Article

@Dao
interface ArticleDao {

    /*
    **Search article that contain the query string in the name or description and sort
    * those articles alphabetically by name.
     */
    @Query("SELECT * FROM articles WHERE" +
            " title LIKE :queryString OR description LIKE :queryString" +
            " ORDER BY title ASC"
    )
    fun articlesByTitle(queryString: String): PagingSource<Int, Article>

    @Query("SELECT * FROM articles WHERE breaking")
    fun breakArticles(): PagingSource<Int, Article>

    @Query("SELECT * FROM articles WHERE favorite")
    fun favoriteArticles(): PagingSource<Int, Article>
    /*
    ** Insert data
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(article: List<Article>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateArticle(article: Article)

    /*
    **clear all data in the articles table
     */
    @Query("DELETE FROM articles WHERE NOT breaking")
    suspend fun clearArticles()

    @Query("DELETE FROM articles WHERE breaking")
    suspend fun clearBreakArticles()
}