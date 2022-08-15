package com.example.newsapp.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newsapp.databinding.ItemArticlePreviewBinding
import com.example.newsapp.models.Article

class ArticleAdapter :
    PagingDataAdapter<Article, ArticleAdapter.ArticleViewHolder>(ARTICLE_DIFF_CALLBACK) {


    class ArticleViewHolder(private val binding: ItemArticlePreviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Article) {

            binding.apply {
                tvTitle.text = item.title
                tvDescription.text = item.description
                tvSource.text = item.source?.name
                tvPublishedAt.text = item.publishedAt
                Glide.with(binding.root.context).load(item.urlToImage).into(ivArticleImage)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val binding = ItemArticlePreviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArticleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = getItem(position)
        article?.let {
            holder.bind(it)
            holder.itemView.setOnClickListener{
                onItemClickListener?.let { it(article) }
            }
        }
    }

    private var onItemClickListener: ((Article)-> Unit)?= null

    fun setOnItemClickListener(listener: (Article)-> Unit) {
        onItemClickListener = listener
    }

    companion object {
        private val ARTICLE_DIFF_CALLBACK = object : DiffUtil.ItemCallback<Article>() {
            override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean =
                oldItem.url == newItem.url

            override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean =
                oldItem == newItem
        }
    }

}