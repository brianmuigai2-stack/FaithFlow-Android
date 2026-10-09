package com.example.faithflow_bible.ui.bible

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.faithflow_bible.R
import com.example.faithflow_bible.data.BookInfo
import com.example.faithflow_bible.data.Testament
import com.example.faithflow_bible.databinding.ItemBookBinding
import com.example.faithflow_bible.databinding.ItemTestamentHeaderBinding

class BooksAdapter(
    private val onBookClick: (BookInfo) -> Unit
) : ListAdapter<BooksRow, RecyclerView.ViewHolder>(RowDiff) {

    class HeaderHolder(val binding: ItemTestamentHeaderBinding) :
        RecyclerView.ViewHolder(binding.root)

    class BookHolder(val binding: ItemBookBinding) : RecyclerView.ViewHolder(binding.root)

    override fun getItemViewType(position: Int) =
        if (getItem(position) is BooksRow.Header) TYPE_HEADER else TYPE_BOOK

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_HEADER) {
            HeaderHolder(ItemTestamentHeaderBinding.inflate(inflater, parent, false))
        } else {
            BookHolder(ItemBookBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val row = getItem(position)) {
            is BooksRow.Header -> (holder as HeaderHolder).binding.testamentTitle.text = row.label.uppercase()
            is BooksRow.Book -> {
                val b = (holder as BookHolder).binding
                val ctx = b.root.context
                b.bookName.text = row.info.name
                b.bookChapters.text = ctx.getString(R.string.ff_chapter_count, row.info.chapterCount)
                // OT = gold accent, NT = blue accent
                val accentColor = if (row.info.testament == Testament.OLD)
                    ContextCompat.getColor(ctx, R.color.ff_accent_gold)
                else
                    ContextCompat.getColor(ctx, R.color.ff_accent_blue)
                b.accentBar.setBackgroundColor(accentColor)
                b.root.setOnClickListener { onBookClick(row.info) }
            }
        }
    }

    private companion object {
        const val TYPE_HEADER = 0
        const val TYPE_BOOK = 1
    }
}

private object RowDiff : DiffUtil.ItemCallback<BooksRow>() {
    override fun areItemsTheSame(oldItem: BooksRow, newItem: BooksRow): Boolean = when {
        oldItem is BooksRow.Header && newItem is BooksRow.Header -> oldItem.label == newItem.label
        oldItem is BooksRow.Book && newItem is BooksRow.Book ->
            oldItem.info.index == newItem.info.index
        else -> false
    }
    override fun areContentsTheSame(oldItem: BooksRow, newItem: BooksRow) = oldItem == newItem
}
