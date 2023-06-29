package com.felixtp.storyappsub2.ui.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.felixtp.storyappsub2.R
import com.felixtp.storyappsub2.data.remote.response.ListStoryItem
import com.felixtp.storyappsub2.databinding.ItemStoryBinding
import com.felixtp.storyappsub2.ui.detail.DetailActivity
import com.felixtp.storyappsub2.ui.utils.withDateFormat

class StoryAdapter(private val context: Context) :
    PagingDataAdapter<ListStoryItem, StoryAdapter.ViewHolder>(DIFF_CALLBACK) {

    inner class ViewHolder(private val binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(story: ListStoryItem) {
            with(binding) {
                Glide.with(root.context)
                    .load(story.photoUrl)
                    .into(imgItemStory)

                tvItemName.text = story.name
                tvItemDescription.text = story.description
                tvItemTime.text = context.getString(R.string.dateFormat, story.createdAt.withDateFormat())

                root.setOnClickListener {
                    val optionCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        context as Activity,
                        Pair(imgItemStory, context.getString(R.string.story_picture)),
                        Pair(tvItemName, context.getString(R.string.name)),
                        Pair(tvItemTime, context.getString(R.string.time)),
                        Pair(tvItemDescription, context.getString(R.string.description))
                    )

                    val intent = Intent(root.context, DetailActivity::class.java).apply {
                        putExtra(DetailActivity.STORY_ID, story.id)
                    }

                    root.context.startActivity(intent, optionCompat.toBundle())
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemStoryBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val story = getItem(position)
        story?.let { holder.bind(it) }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}
