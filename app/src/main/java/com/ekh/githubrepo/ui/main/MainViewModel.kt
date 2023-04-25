package com.ekh.githubrepo.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ekh.githubrepo.data.Repo
import com.ekh.githubrepo.datasource.GithubRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: GithubRepository,
) : ViewModel() {
    private val _uiState: MutableStateFlow<UiModel> = MutableStateFlow(UiModel())
    val uiState: StateFlow<UiModel> = _uiState.asStateFlow()
    private var currentPage: Int = 1

    private val _loadingCounterState: MutableStateFlow<Int> = MutableStateFlow(0)
    private val loadingCounter: AtomicInteger = AtomicInteger(0)

    private val exceptionHandler: CoroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        _uiState.update {
            Timber.d("__ exception : $throwable")
            it.copy(errorMessage = throwable.message)
        }
    }

    init {
        viewModelScope.launch(exceptionHandler) {
            _loadingCounterState.collect { count ->
                _uiState.update {
                    it.copy(isLoading = count > 0)
                }
            }
        }
    }

    fun clearErrorText() {
        viewModelScope.launch(exceptionHandler) {
            _uiState.update {
                it.copy(errorMessage = null)
            }
        }
    }

    fun updateQuery(query: String) {
        if (uiState.value.query == query) return
        Timber.d("__ updateQuery: $query")
        viewModelScope.launch(exceptionHandler) {
            _uiState.update {
                it.copy(
                    query = query
                )
            }
        }
    }

    fun search() {
        viewModelScope.withLoading(exceptionHandler) {
            Timber.d("__ search")
            currentPage = 1
            val query = uiState.value.query
            val result = repository.search(query, currentPage)
            _uiState.update {
                it.copy(
                    itemList = result.items
                )
            }
        }
    }

    fun loadNextPage() {
        viewModelScope.withLoading(exceptionHandler) {
            currentPage++
            Timber.d("__ loadNextPage: $currentPage")
            val query = uiState.value.query
            val result = repository.search(query, currentPage)
            val itemList = result.items
            _uiState.update {
                it.copy(
                    itemList = it.itemList + itemList
                )
            }
        }
    }


    private fun <T> CoroutineScope.withLoading(
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> T,
    ) {
        launch(context, start) {
            try {
                _loadingCounterState.emit(loadingCounter.incrementAndGet())
                block(this)
            } finally {
                _loadingCounterState.emit(loadingCounter.decrementAndGet())
            }
        }
    }

    data class UiModel(
        val query: String = "",
        val itemList: List<Repo> = listOf(),
        val isLoading: Boolean = false,
        val errorMessage: String? = null,
    )
}