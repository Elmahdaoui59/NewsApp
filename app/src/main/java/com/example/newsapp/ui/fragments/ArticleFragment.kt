package com.example.newsapp.ui.fragments

import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.newsapp.R
import com.example.newsapp.ui.ArticleViewModel
import com.example.newsapp.ui.NewsActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ArticleFragment : Fragment(R.layout.fragment_article) {
    lateinit var viewModel: ArticleViewModel
    val args: ArticleFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as NewsActivity).viewModel

        val article = args.article

        val webView = view.findViewById<WebView>(R.id.webView)
        webView.apply {
            webViewClient = WebViewClient()
            loadUrl(article.url)
        }
        val fab = view.findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            article.favorite = true
            viewModel.updateArticle(article)
        }
    }
}