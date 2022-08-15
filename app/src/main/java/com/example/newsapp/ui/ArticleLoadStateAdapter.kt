package com.example.newsapp.ui

import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter

class ArticleLoadStateAdapter(
    private val retry: () -> Unit
): LoadStateAdapter<ArticleLoadStateViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): ArticleLoadStateViewHolder {
        return ArticleLoadStateViewHolder.create(parent, retry)
    }

    override fun onBindViewHolder(holder: ArticleLoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

}