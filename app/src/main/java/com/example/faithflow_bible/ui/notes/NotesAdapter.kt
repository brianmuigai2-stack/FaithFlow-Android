package com.example.faithflow_bible.ui.notes

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.faithflow_bible.data.Note
import com.example.faithflow_bible.databinding.ItemNoteBinding
import java.text.DateFormat
import java.util.Date

class NotesAdapter(
    private val onClick: (Note) -> Unit
) : ListAdapter<Note, NotesAdapter.ViewHolder>(NoteDiff) {

    class ViewHolder(val binding: ItemNoteBinding) : RecyclerView.ViewHolder(binding.root)

    private val dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val note = getItem(position)
        with(holder.binding) {
            noteTitle.text = note.title
            noteMeta.text = listOf(note.reference, dateFormat.format(Date(note.updatedAt)))
                .filter { it.isNotBlank() }
                .joinToString(" \u00B7 ")
            notePreview.text = note.content
            notePreview.isVisible = note.content.isNotBlank()
            root.setOnClickListener { onClick(note) }
        }
    }
}

private object NoteDiff : DiffUtil.ItemCallback<Note>() {
    override fun areItemsTheSame(oldItem: Note, newItem: Note) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Note, newItem: Note) = oldItem == newItem
}