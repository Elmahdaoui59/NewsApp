package com.example.newsapp.db

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState.Loading.endOfPaginationReached
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.newsapp.api.ApiClient
import com.example.newsapp.models.Article
import com.example.newsapp.util.Constants.Companion.STARTING_PAGE_NUMBER
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class ArticleRemoteMediator(
    private val query: String?,
    private val api: ApiClient,
    private val articleDatabase: ArticleDatabase
) : RemoteMediator<Int, Article>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Article>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: STARTING_PAGE_NUMBER
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                if (prevKey == null) {
                    return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                }
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                if (nextKey == null) {
                    return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                }
                nextKey
            }
        }
        try {

            if (query != null) {


                val apiResponse = api.searchNews(query, page, state.config.pageSize)
                val articles = apiResponse.articles
                val endOfPaginationReached = articles.isEmpty()
                articleDatabase.withTransaction {
                    //clear all tables in the database
                    if (loadType == LoadType.REFRESH) {
                        articleDatabase.remoteKeysDao().clearRemoteKeys()
                        articleDatabase.articleDao().clearArticles()
                    }
                    val prevKey = if (page == STARTING_PAGE_NUMBER) null else page - 1
                    val nextKey = if (endOfPaginationReached) null else page + 1
                    val keys = articles.map {
                        RemoteKeys(articleId = it.id, prevKey, nextKey)
                    }
                    articleDatabase.remoteKeysDao().insertAll(keys)
                    articleDatabase.articleDao().insertAll(articles)
                }

            } else {
                val apiResponse = api.getNews(page)
                val articles = apiResponse.articles
                val endOfPaginationReached = articles.isEmpty()
                articleDatabase.withTransaction {
                    //clear all tables in the database
                    if (loadType == LoadType.REFRESH) {
                        articleDatabase.remoteKeysDao().clearBreakRemoteKeys()
                        articleDatabase.articleDao().clearBreakArticles()
                    }
                    val prevKey = if (page == STARTING_PAGE_NUMBER) null else page - 1
                    val nextKey = if (endOfPaginationReached) null else page + 1
                    val keys = articles.map {
                        RemoteKeys(articleId = it.id, prevKey, nextKey, breaking = true)
                    }
                    val breakArticles = articles.map {
                        it.copy(breaking = true)
                    }
                    articleDatabase.remoteKeysDao().insertAll(keys)
                    articleDatabase.articleDao().insertAll(breakArticles)
                }
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }
    }


private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, Article>): RemoteKeys? {
    return state.pages.lastOrNull() { it.data.isNotEmpty() }?.data?.lastOrNull()
        ?.let { article ->
            articleDatabase.remoteKeysDao().remoteKeysArticleId(article.id)
        }
}

private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, Article>): RemoteKeys? {
    return state.pages.firstOrNull() { it.data.isNotEmpty() }?.data?.firstOrNull()
        ?.let { article ->
            articleDatabase.remoteKeysDao().remoteKeysArticleId(article.id)
        }
}

private suspend fun getRemoteKeyClosestToCurrentPosition(
    state: PagingState<Int, Article>
): RemoteKeys? {
    return state.anchorPosition?.let { position ->
        state.closestItemToPosition(position)?.id?.let { articleId ->
            articleDatabase.remoteKeysDao().remoteKeysArticleId(articleId)
        }
    }
}

}