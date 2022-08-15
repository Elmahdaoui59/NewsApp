package com.example.newsapp.ui.fragments

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.R
import com.example.newsapp.databinding.FragmentSearchNewsBinding
import com.example.newsapp.models.Article
import com.example.newsapp.ui.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SearchNewsFragment : Fragment() {
    lateinit var viewModel: ArticleViewModel
    private lateinit var binding: FragmentSearchNewsBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as NewsActivity).viewModel

        binding.bindState(
            viewModel.state,
            pagingData = viewModel.pagingDataFlow,
            uiActions = viewModel.accept,
        )


    }

    private fun FragmentSearchNewsBinding.bindState(
        uiState: StateFlow<UiState>,
        pagingData: Flow<PagingData<Article>>,
        uiActions: (UiAction) -> Unit,
    ) {

        val articleAdapter = ArticleAdapter()
        val header = ArticleLoadStateAdapter { articleAdapter.retry() }
        binding.rvSearchNews.apply {
            layoutManager = LinearLayoutManager(activity as NewsActivity)
            adapter = articleAdapter.withLoadStateHeaderAndFooter(
                header = header,
                footer = ArticleLoadStateAdapter { articleAdapter.retry() }
            )
            setHasFixedSize(true)
        }
        articleAdapter.setOnItemClickListener {
            val bundle = Bundle().apply { putSerializable("article", it) }
            findNavController().navigate(
                R.id.action_searchNewsFragment_to_articleFragment, bundle
            )
        }
        bindSearch(
            uiState = uiState,
            onQueryChanged = uiActions
        )
        bindList(
            header = header,
            articleAdapter = articleAdapter,
            uiState = uiState,
            pagingData = pagingData,
            onScrollChanged = uiActions
        )
    }

    private fun FragmentSearchNewsBinding.bindList(
        header: ArticleLoadStateAdapter,
        articleAdapter: ArticleAdapter,
        uiState: StateFlow<UiState>,
        pagingData: Flow<PagingData<Article>>,
        onScrollChanged: (UiAction.Scroll) -> Unit
    ) {
        retryButton.setOnClickListener { articleAdapter.retry() }

        rvSearchNews.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy != 0) onScrollChanged(UiAction.Scroll(currentQuery = uiState.value.query))
            }
        })
        val notLoading = articleAdapter.loadStateFlow
            .asRemotePresentationState()
            .map { it == RemotePresentationState.PRESENTED }

        val hasNotScrolledForCurrentSearch = uiState
            .map { it.hasNotScrolledForCurrentSearch }
            .distinctUntilChanged()

        val shouldScrollToTop = combine(
            notLoading,
            hasNotScrolledForCurrentSearch,
            Boolean::and
        )
            .distinctUntilChanged()
        lifecycleScope.launch {
            pagingData.collectLatest(articleAdapter::submitData)
        }
        lifecycleScope.launch {
            shouldScrollToTop.collect { shouldScrollToTop ->
                if (shouldScrollToTop) rvSearchNews.scrollToPosition(0)
            }
        }
        lifecycleScope.launch {
            articleAdapter.loadStateFlow.collect { loadState ->
                val isListEmpty =
                    loadState.refresh is LoadState.NotLoading && articleAdapter.itemCount == 0
                emptyList.isVisible = isListEmpty
                rvSearchNews.isVisible = loadState.source.refresh is LoadState.NotLoading || loadState.mediator?.refresh is LoadState.NotLoading
                progressBar.isVisible =  loadState.mediator?.refresh is LoadState.Loading
                retryButton.isVisible = loadState.mediator?.refresh is LoadState.Error && articleAdapter.itemCount == 0

                header.loadState = loadState.mediator
                    ?.refresh
                    ?.takeIf { it is LoadState.Error && articleAdapter.itemCount > 0 }
                    ?: loadState.prepend

            }
        }

    }

    private fun FragmentSearchNewsBinding.bindSearch(
        uiState: StateFlow<UiState>,
        onQueryChanged: (UiAction.Search) -> Unit
    ) {
        etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                updateArticleListFromInput(onQueryChanged)
                true
            } else false
        }

        etSearch.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                updateArticleListFromInput(onQueryChanged)
                true
            } else false
        }

        lifecycleScope.launch {
            uiState
                .map { it.query }
                .distinctUntilChanged()
                .collect(etSearch::setText)
        }

    }

    private fun FragmentSearchNewsBinding.updateArticleListFromInput(onQueryChanged: (UiAction.Search) -> Unit) {
        etSearch.text.trim().let {
            if (it.isNotEmpty()) {
                onQueryChanged(UiAction.Search(query = it.toString()))
            }
        }
    }
}