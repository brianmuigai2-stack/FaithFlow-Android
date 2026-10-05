package com.example.faithflow_bible.ui.bible

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.faithflow_bible.R
import com.example.faithflow_bible.data.BibleRepository
import com.example.faithflow_bible.data.BookInfo

data class ChaptersUi(val book: BookInfo?, val chapters: List<Int>, val subtitle: String)

class ChaptersViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = BibleRepository.get(app)
    private val chooseChapter = app.getString(R.string.ff_choose_chapter)

    private val _ui = MutableLiveData<ChaptersUi>()
    val ui: LiveData<ChaptersUi> = _ui

    /** Only the first call does anything, so rotating doesn't reset the book. */
    fun load(bookIndex: Int) {
        if (_ui.value != null) return
        val book = repo.book(bookIndex)
        _ui.value = ChaptersUi(
            book = book,
            chapters = book?.let { (1..it.chapterCount).toList() } ?: emptyList(),
            subtitle = chooseChapter
        )
    }
}