package com.example.faithflow_bible.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.faithflow_bible.data.Verse
import com.example.faithflow_bible.databinding.ItemRecentSavedBinding

class RecentSavedAdapter(
    private val onClick: (Verse) -> Unit
) : ListAdapter<Verse, RecentSavedAdapter.ViewHolder>(VerseDiff) {

    class ViewHolder(val binding: ItemRecentSavedBinding) : RecyclerView.ViewHolder(binding.root)

    private val accents = intArrayOf(0xFF1FA39A.toInt(), 0xFFF2B84B.toInt(), 0xFF4C7FD8.toInt())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemRecentSavedBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val verse = getItem(position)
        with(holder.binding) {
            reference.text = verse.reference
            preview.text = "\u201C${verse.text}\u201D"
            translation.text = verse.translation
            iconBg.setCardBackgroundColor(accents[position % accents.size])
            root.setOnClickListener { onClick(verse) }
        }
    }
}

private object VerseDiff : DiffUtil.ItemCallback<Verse>() {
    override fun areItemsTheSame(oldItem: Verse, newItem: Verse) = oldItem.reference == newItem.reference
    override fun areContentsTheSame(oldItem: Verse, newItem: Verse) = oldItem == newItem
}
