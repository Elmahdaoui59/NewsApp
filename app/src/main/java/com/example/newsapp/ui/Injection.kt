package com.example.newsapp.ui

import android.content.Context
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.savedstate.SavedStateRegistryOwner
import com.example.newsapp.api.ApiClient
import com.example.newsapp.datasource.ArticlesRepository
import com.example.newsapp.db.ArticleDatabase

object Injection {
    class ViewModelFactory(
        owner: SavedStateRegistryOwner,
        private val repository: ArticlesRepository
    ) : AbstractSavedStateViewModelFactory(owner, null) {
        override fun <T : ViewModel?> create(
            key: String,
            modelClass: Class<T>,
            handle: SavedStateHandle
        ): T {
            return if (modelClass.isAssignableFrom(ArticleViewModel::class.java)) {
                ArticleViewModel(repository, handle) as T
            } else {
                throw java.lang.IllegalArgumentException("ViewModel Not Found")
            }
        }

    }

    private fun provideArticleRepository(context: Context): ArticlesRepository {
        return ArticlesRepository(ApiClient, ArticleDatabase.getInstance(context) )
    }


    fun provideViewModelFactory(
        context: Context,
        owner: SavedStateRegistryOwner
    ): ViewModelProvider.Factory {
        return ViewModelFactory(owner, provideArticleRepository(context))
    }
}