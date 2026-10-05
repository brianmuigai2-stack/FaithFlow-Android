package com.example.faithflow_bible.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.faithflow_bible.data.FavoriteVerse
import com.example.faithflow_bible.data.HighlightColors
import com.example.faithflow_bible.databinding.ItemRecentSavedBinding

/** Verse rows used on Home (recent) and on the Favorites screen. */
class RecentSavedAdapter(
    private val onClick: (FavoriteVerse) -> Unit
) : ListAdapter<FavoriteVerse, RecentSavedAdapter.ViewHolder>(FavoriteDiff) {

    class ViewHolder(val binding: ItemRecentSavedBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemRecentSavedBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val fav = getItem(position)
        with(holder.binding) {
            reference.text = fav.reference
            preview.text = "\u201C${fav.text}\u201D"
            translation.text = "WEB"
            // highlighted verses show their highlight colour, saved-only verses show teal
            iconBg.setCardBackgroundColor(HighlightColors.swatches.getOrNull(fav.highlight) ?: TEAL)
            root.setOnClickListener { onClick(fav) }
        }
    }

    private companion object {
        val TEAL = 0xFF1FA39A.toInt()
    }
}

private object FavoriteDiff : DiffUtil.ItemCallback<FavoriteVerse>() {
    override fun areItemsTheSame(oldItem: FavoriteVerse, newItem: FavoriteVerse) =
        oldItem.bookIndex == newItem.bookIndex && oldItem.chapter == newItem.chapter &&
            oldItem.verse == newItem.verse

    override fun areContentsTheSame(oldItem: FavoriteVerse, newItem: FavoriteVerse) = oldItem == newItem
}