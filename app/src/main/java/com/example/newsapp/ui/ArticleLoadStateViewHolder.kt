package com.example.newsapp.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.R
import com.example.newsapp.databinding.ArticleLoadStateFooterViewItemBinding

class ArticleLoadStateViewHolder(
    private val binding: ArticleLoadStateFooterViewItemBinding,
    retry: () -> Unit
): RecyclerView.ViewHolder(binding.root) {
    init {
        binding.retryButton.setOnClickListener { retry.invoke() }
    }

    fun bind(loadState: LoadState) {
        if (loadState is LoadState.Error) {
            binding.errorMsg.text = loadState.error.localizedMessage
        }
        binding.progressBar.isVisible = loadState is LoadState.Loading
        binding.retryButton.isVisible = loadState is LoadState.Error
        binding.errorMsg.isVisible = loadState is LoadState.Error
    }

    companion object {
        fun create(parent: ViewGroup, retry: () -> Unit): ArticleLoadStateViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.article_load_state_footer_view_item,parent, false)
            val binding = ArticleLoadStateFooterViewItemBinding.bind(view)

            return ArticleLoadStateViewHolder(binding, retry)
        }
    }
}