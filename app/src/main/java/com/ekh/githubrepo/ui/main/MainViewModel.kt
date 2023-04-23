package com.ekh.githubrepo.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ekh.githubrepo.data.Repo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class MainViewModel : ViewModel() {
    private val _uiState: MutableStateFlow<UiModel> = MutableStateFlow(UiModel())
    val uiState: StateFlow<UiModel> = _uiState.asStateFlow()

    fun updateQuery(query: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    query = query
                )
            }
        }
    }

    data class UiModel(
        val query: String = "",
        val itemList: List<Repo> = listOf()
    )
}