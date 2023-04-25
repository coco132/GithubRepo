package com.ekh.githubrepo.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ekh.githubrepo.data.Repo
import com.ekh.githubrepo.datasource.GithubRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: GithubRepository,
) : ViewModel() {
    private val _uiState: MutableStateFlow<UiModel> = MutableStateFlow(UiModel())
    val uiState: StateFlow<UiModel> = _uiState.asStateFlow()
    private var currentPage: Int = 0

    fun updateQuery(query: String) {
        if (uiState.value.query == query) return
        Timber.d("__ updateQuery: $query")
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    query = query
                )
            }
        }
    }

    fun search() {
        viewModelScope.launch {
            Timber.d("__ search")
            currentPage = 0
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
        viewModelScope.launch {
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

    data class UiModel(
        val query: String = "",
        val itemList: List<Repo> = listOf(),
    )
}