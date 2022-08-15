package com.example.newsapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsapp.R
import com.example.newsapp.databinding.FragmentSavedNewsBinding
import com.example.newsapp.ui.ArticleAdapter
import com.example.newsapp.ui.ArticleLoadStateAdapter
import com.example.newsapp.ui.ArticleViewModel
import com.example.newsapp.ui.NewsActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SavedNewsFragment: Fragment() {
    lateinit var viewModel: ArticleViewModel
    lateinit var binding: FragmentSavedNewsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSavedNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as NewsActivity).viewModel

        val articleAdapter = ArticleAdapter()

        binding.rvSavedNews.apply {
            layoutManager = LinearLayoutManager(activity as NewsActivity)
            adapter = articleAdapter
            setHasFixedSize(true)
        }
        viewLifecycleOwner.lifecycleScope.launch{
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.favArticles.collectLatest {
                    articleAdapter.submitData(it)
                }
            }
        }
        articleAdapter.setOnItemClickListener {
            val bundle = Bundle().apply { putSerializable("article", it) }
            findNavController().navigate(
                R.id.action_savedNewsFragment_to_articleFragment, bundle
            )
        }
    }
}