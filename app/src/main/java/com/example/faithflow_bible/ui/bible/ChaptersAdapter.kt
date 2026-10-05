package com.example.faithflow_bible.ui.bible

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.faithflow_bible.databinding.ItemChapterBinding

/** The 1..N grid for one book. */
class ChaptersAdapter(
    private val onChapterClick: (Int) -> Unit
) : ListAdapter<Int, ChaptersAdapter.ViewHolder>(ChapterDiff) {

    class ViewHolder(val binding: ItemChapterBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemChapterBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chapter = getItem(position)
        holder.binding.chapterNumber.text = chapter.toString()
        holder.binding.root.setOnClickListener { onChapterClick(chapter) }
    }
}

private object ChapterDiff : DiffUtil.ItemCallback<Int>() {
    override fun areItemsTheSame(oldItem: Int, newItem: Int) = oldItem == newItem
    override fun areContentsTheSame(oldItem: Int, newItem: Int) = oldItem == newItem
}