package com.example.faithflow_bible.ui.bible

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.faithflow_bible.data.BibleRepository
import com.example.faithflow_bible.data.BookInfo

sealed interface BooksRow {
    data class Header(val label: String) : BooksRow
    data class Book(val info: BookInfo) : BooksRow
}

class BooksViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = BibleRepository.get(app)
    private val allRows: List<BooksRow> = buildList {
        add(BooksRow.Header("Old Testament"))
        repo.oldTestamentBooks.forEach { add(BooksRow.Book(it)) }
        add(BooksRow.Header("New Testament"))
        repo.newTestamentBooks.forEach { add(BooksRow.Book(it)) }
    }

    private val _rows = MutableLiveData(allRows)
    val rows: LiveData<List<BooksRow>> = _rows

    fun filter(query: String) {
        if (query.isBlank()) {
            _rows.value = allRows
            return
        }
        val q = query.trim().lowercase()
        // Keep headers only if they have matching books beneath them
        val filtered = mutableListOf<BooksRow>()
        var pendingHeader: BooksRow.Header? = null
        for (row in allRows) {
            when (row) {
                is BooksRow.Header -> pendingHeader = row
                is BooksRow.Book -> if (row.info.name.lowercase().contains(q)) {
                    if (pendingHeader != null) {
                        filtered += pendingHeader
                        pendingHeader = null
                    }
                    filtered += row
                }
            }
        }
        _rows.value = filtered
    }
}
