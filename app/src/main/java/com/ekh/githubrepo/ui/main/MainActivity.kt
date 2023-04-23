package com.ekh.githubrepo.ui.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import com.ekh.githubrepo.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        val viewModel by viewModels<MainViewModel>()
        bindSearchView(binding.etSearch, viewModel)
        bindRecyclerView(binding.rvList, viewModel)
    }

    private fun bindSearchView(view: SearchView, viewModel: MainViewModel) {
        view.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                TODO("Not yet implemented")
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // TODO: 체크 추가
                viewModel.updateQuery(newText ?: "")
                return true
            }
        })

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.map { it.query }.distinctUntilChanged().collectLatest {
                    view.setQuery(it, false)
                }
            }
        }
    }

    private fun bindRecyclerView(view: RecyclerView, viewModel: MainViewModel) {
        val adapter = MainListAdapter()
        view.adapter = adapter

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.map { it.itemList }.distinctUntilChanged().collectLatest {
                    adapter.submitList(it)
                }
            }
        }


    }


}