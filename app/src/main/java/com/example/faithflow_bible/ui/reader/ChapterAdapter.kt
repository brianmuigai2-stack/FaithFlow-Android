package com.example.faithflow_bible.ui.reader

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.faithflow_bible.R
import com.example.faithflow_bible.data.HighlightColors
import com.example.faithflow_bible.databinding.ItemChapterHeadingBinding
import com.example.faithflow_bible.databinding.ItemVerseBinding

class ChapterAdapter(
    private var textSizeSp: Float,
    private val onVerseClick: (ReaderRow.VerseItem) -> Unit
) : ListAdapter<ReaderRow, RecyclerView.ViewHolder>(RowDiff) {

    private class HeadingHolder(val binding: ItemChapterHeadingBinding) :
        RecyclerView.ViewHolder(binding.root)

    private class VerseHolder(val binding: ItemVerseBinding) :
        RecyclerView.ViewHolder(binding.root)

    fun setTextSize(sp: Float) {
        textSizeSp = sp
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int) =
        if (getItem(position) is ReaderRow.Heading) TYPE_HEADING else TYPE_VERSE

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_HEADING) {
            HeadingHolder(ItemChapterHeadingBinding.inflate(inflater, parent, false))
        } else {
            VerseHolder(ItemVerseBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val row = getItem(position)) {
            is ReaderRow.Heading -> (holder as HeadingHolder).binding.chapterTitle.text = row.title
            is ReaderRow.VerseItem -> bindVerse((holder as VerseHolder).binding, row)
        }
    }

    private fun bindVerse(b: ItemVerseBinding, row: ReaderRow.VerseItem) {
        val ctx = b.root.context
        b.verseNumber.text = row.number.toString()
        b.verseBody.text = row.text
        b.verseBody.textSize = textSizeSp

        val fill = when {
            row.highlight in HighlightColors.swatches.indices ->
                ColorUtils.setAlphaComponent(HighlightColors.swatches[row.highlight], 0x55)
            row.focused -> ContextCompat.getColor(ctx, R.color.ff_primary_container)
            else -> Color.TRANSPARENT
        }
        b.root.background = GradientDrawable().apply {
            cornerRadius = 12f * ctx.resources.displayMetrics.density
            setColor(fill)
        }
        b.root.setOnClickListener { onVerseClick(row) }
    }

    private companion object {
        const val TYPE_HEADING = 0
        const val TYPE_VERSE = 1
    }
}

private object RowDiff : DiffUtil.ItemCallback<ReaderRow>() {
    override fun areItemsTheSame(oldItem: ReaderRow, newItem: ReaderRow): Boolean = when {
        oldItem is ReaderRow.Heading && newItem is ReaderRow.Heading -> true
        oldItem is ReaderRow.VerseItem && newItem is ReaderRow.VerseItem -> oldItem.number == newItem.number
        else -> false
    }

    override fun areContentsTheSame(oldItem: ReaderRow, newItem: ReaderRow) = oldItem == newItem
}
