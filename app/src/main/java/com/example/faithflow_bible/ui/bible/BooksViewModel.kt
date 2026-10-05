package com.example.faithflow_bible.ui.bible

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.faithflow_bible.data.BibleRepository
import com.example.faithflow_bible.data.BookInfo

/** A testament heading or a book row, so one list can hold both. */
sealed interface BooksRow {
    data class Header(val label: String) : BooksRow
    data class Book(val info: BookInfo) : BooksRow
}

class BooksViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = BibleRepository.get(app)

    // The index is small and already loaded, so this is cheap enough to build inline.
    val rows: LiveData<List<BooksRow>> = MutableLiveData(
        buildList {
            add(BooksRow.Header("Old Testament"))
            repo.oldTestamentBooks.forEach { add(BooksRow.Book(it)) }
            add(BooksRow.Header("New Testament"))
            repo.newTestamentBooks.forEach { add(BooksRow.Book(it)) }
        }
    )
}