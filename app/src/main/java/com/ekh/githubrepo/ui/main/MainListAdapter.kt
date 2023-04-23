package com.ekh.githubrepo.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.ekh.githubrepo.R
import com.ekh.githubrepo.data.Repo
import com.ekh.githubrepo.databinding.ItemRepoBinding

class MainListAdapter : ListAdapter<Repo, ViewHolder>(DiffUtilCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRepoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RepoViewHolder(binding)
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return if (item is Repo) {
            R.layout.item_repo
        } else throw java.lang.IllegalStateException()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is RepoViewHolder -> {
                holder.bind(item)
            }
        }
    }

    class RepoViewHolder(private val binding: ItemRepoBinding) : ViewHolder(binding.root) {
        fun bind(item: Repo) {
            binding.tvRepoName.text = item.name
            binding.tvDescription.text = item.description
        }
    }
}

private object DiffUtilCallback : DiffUtil.ItemCallback<Repo>() {
    override fun areItemsTheSame(oldItem: Repo, newItem: Repo): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Repo, newItem: Repo): Boolean {
        return oldItem == newItem
    }
}