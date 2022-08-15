package com.example.newsapp.datasource

import androidx.paging.*
import com.example.newsapp.api.ApiClient
import com.example.newsapp.db.ArticleDatabase
import com.example.newsapp.db.ArticleRemoteMediator
import com.example.newsapp.models.Article
import com.example.newsapp.util.Constants
import kotlinx.coroutines.flow.Flow

class ArticlesRepository(
    private val api: ApiClient,
    private val database: ArticleDatabase
) {

    @OptIn(ExperimentalPagingApi::class)
    fun getBreakNews(): Flow<PagingData<Article>> {
        val pagingSourceFactory = { database.articleDao().breakArticles() }
        return Pager(
            config = PagingConfig(pageSize = Constants.pageSize, enablePlaceholders = false),
            remoteMediator = ArticleRemoteMediator(null, api, database),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    @OptIn(ExperimentalPagingApi::class)
    fun getSearchResult(query: String): Flow<PagingData<Article>> {
        val dbQuery = "%${query.replace(' ', '%')}%"
        val pagingSourceFactory = { database.articleDao().articlesByTitle(dbQuery) }
        return Pager(
            config = PagingConfig(
                pageSize = Constants.pageSize, enablePlaceholders = false
            ),
            remoteMediator = ArticleRemoteMediator(query, api, database),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    suspend fun updateArticle(article: Article) {
        database.articleDao().updateArticle(article)
    }

    fun getFavoriteArticles(): Flow<PagingData<Article>> {
        val pagingSourceFactory = {
            database.articleDao().favoriteArticles()
        }
        return Pager(
            config = PagingConfig(
                pageSize = Constants.pageSize, enablePlaceholders = false
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }
}