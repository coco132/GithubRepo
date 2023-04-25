package com.ekh.githubrepo.ui.main

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.FrameLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.view.isInvisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ekh.githubrepo.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val viewModel by viewModels<MainViewModel>()
        bindSearchView(binding.etSearch, viewModel)
        bindRecyclerView(binding.rvList, viewModel)
        bindLoading(binding.pbLoading, viewModel)
    }

    private fun bindSearchView(view: AppCompatEditText, viewModel: MainViewModel) {
        view.addTextChangedListener {
            val text = it?.toString() ?: ""
            viewModel.updateQuery(text)
        }

        view.setOnEditorActionListener { _, actionId, _ ->
            if (actionId != EditorInfo.IME_ACTION_SEARCH) return@setOnEditorActionListener true
            viewModel.search()
            true
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.map { it.query }.distinctUntilChanged().collectLatest {
                    if (it == view.text.toString()) return@collectLatest
                    view.setText(it)
                    view.setSelection(view.length())
                }
            }
        }
    }

    private fun bindRecyclerView(view: RecyclerView, viewModel: MainViewModel) {
        val adapter = MainListAdapter()
        view.adapter = adapter

        view.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (viewModel.uiState.value.isLoading) return
                    val lastPosition =
                        (recyclerView.layoutManager as? LinearLayoutManager)?.findLastCompletelyVisibleItemPosition() ?: 0
                    val totalCount = recyclerView.adapter?.itemCount ?: 0
                    if (lastPosition < totalCount - 1) return
                    viewModel.loadNextPage()
                    Timber.d("__ loadNextPage")
                }
            }
        )

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.map { it.itemList }.distinctUntilChanged().collectLatest {
                    adapter.submitList(it)
                }
            }
        }
    }


    private fun bindLoading(view: FrameLayout, viewModel: MainViewModel) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.map { it.isLoading }.distinctUntilChanged().collectLatest {
                    view.isInvisible = !it
                }
            }
        }
    }


}