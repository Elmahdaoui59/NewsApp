package com.example.newsapp.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.newsapp.datasource.ArticlesRepository
import com.example.newsapp.models.Article
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ArticleViewModel(
    private val repository: ArticlesRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    val articles: Flow<PagingData<Article>> = repository.getBreakNews()

    val favArticles = repository.getFavoriteArticles()

    val state: StateFlow<UiState>

    val pagingDataFlow: Flow<PagingData<Article>>

    val accept: (UiAction) -> Unit

    init {
        val initialQuery: String = savedStateHandle[LAST_SEARCH_QUERY] ?: DEFAULT_QUERY
        val lastQueryScrolled = savedStateHandle[LAST_QUERY_SCROLLED] ?: DEFAULT_QUERY
        val actionStateFlow = MutableSharedFlow<UiAction>()
        val searches = actionStateFlow
            .filterIsInstance<UiAction.Search>()
            .distinctUntilChanged()
            .onStart { emit(UiAction.Search(query = initialQuery)) }
        val queriesScrolled = actionStateFlow
            .filterIsInstance<UiAction.Scroll>()
            .distinctUntilChanged()
            .shareIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
                replay = 1
            )
            .onStart { emit(UiAction.Scroll(currentQuery = lastQueryScrolled)) }
        pagingDataFlow = searches
            .flatMapLatest { searchRepo(queryString = it.query) }
            .cachedIn(viewModelScope)
        state = combine(
            searches,
            queriesScrolled,
            ::Pair
        ).map { (search, scroll) ->
            UiState(
                query = search.query,
                lastQueryScrolled = scroll.currentQuery,
                hasNotScrolledForCurrentSearch = search.query != scroll.currentQuery
            )
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
                initialValue = UiState()
            )

        accept = { action ->
            viewModelScope.launch { actionStateFlow.emit(action) }
        }
    }

    private fun searchRepo(queryString: String): Flow<PagingData<Article>> =
        repository.getSearchResult(queryString)

    fun updateArticle(article: Article) {
        viewModelScope.launch {
            repository.updateArticle(article)
        }
    }

    override fun onCleared() {
        savedStateHandle[LAST_SEARCH_QUERY] = state.value.query
        savedStateHandle[LAST_QUERY_SCROLLED] = state.value.lastQueryScrolled
        super.onCleared()
    }
}

sealed class UiAction {
    data class Search(val query: String) : UiAction()
    data class Scroll(val currentQuery: String) : UiAction()
}

data class UiState(
    val query: String = DEFAULT_QUERY,
    val lastQueryScrolled: String = DEFAULT_QUERY,
    val hasNotScrolledForCurrentSearch: Boolean = false
)

private const val DEFAULT_QUERY = "volcano"
private const val LAST_QUERY_SCROLLED = "last_query_scrolled"
private const val LAST_SEARCH_QUERY: String = "last_search_query"
