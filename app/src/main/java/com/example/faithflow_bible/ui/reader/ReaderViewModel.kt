package com.example.faithflow_bible.ui.reader

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.faithflow_bible.data.BibleRepository
import com.example.faithflow_bible.data.ChapterVerse
import com.example.faithflow_bible.data.ReaderPrefs
import com.example.faithflow_bible.data.Translation
import java.util.concurrent.Executors

sealed interface ReaderRow {
    data class Heading(val title: String) : ReaderRow
    data class VerseItem(
        val number: Int,
        val text: String,
        val highlight: Int,
        val focused: Boolean
    ) : ReaderRow
}

data class ReaderState(
    val bookName: String,
    val translation: Translation,
    val rows: List<ReaderRow>,
    val hasPrevious: Boolean,
    val hasNext: Boolean,
    val totalChapters: Int,
    val scrollToRow: Int
)

class ReaderViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = BibleRepository.get(app)
    private val prefs = ReaderPrefs(app)
    private val executor = Executors.newSingleThreadExecutor()

    private val _state = MutableLiveData<ReaderState>()
    val state: LiveData<ReaderState> = _state

    var bookIndex = -1
        private set
    var chapter = 0
        private set
    val bookName: String get() = repo.books[bookIndex].name

    /** Everything bundled in assets/bible, WEB first. */
    val translations: List<Translation> get() = repo.availableTranslations

    /** The translation currently on screen. */
    val translation: Translation get() = repo.translation

    @Volatile
    private var loaded: List<ChapterVerse> = emptyList()
    private var focusVerse = 0

    /** Only the first call does anything, so rotating the screen doesn't reset the page. */
    fun start(book: Int, chapter: Int, verse: Int) {
        if (bookIndex == -1) open(book, chapter, verse)
    }

    fun open(book: Int, chapter: Int, verse: Int = 0) {
        val b = book.coerceIn(0, repo.books.lastIndex)
        val c = chapter.coerceIn(1, repo.books[b].chapterCount)
        bookIndex = b
        this.chapter = c
        focusVerse = verse
        load(b, c, verse, scroll = true)
    }

    /** Switching translation keeps the reader on the same chapter and scroll position. */
    fun selectTranslation(translation: Translation) {
        if (translation == repo.translation) return
        repo.translation = translation
        if (bookIndex != -1) load(bookIndex, chapter, focusVerse, scroll = false)
    }

    private fun load(b: Int, c: Int, focus: Int, scroll: Boolean) {
        executor.execute {
            val verses = repo.loadChapter(b, c)
            loaded = verses
            _state.postValue(buildState(b, c, verses, focus, scroll))
        }
    }

    fun next() {
        val book = repo.books[bookIndex]
        when {
            chapter < book.chapterCount -> open(bookIndex, chapter + 1)
            bookIndex < repo.books.lastIndex -> open(bookIndex + 1, 1)
        }
    }

    fun previous() {
        when {
            chapter > 1 -> open(bookIndex, chapter - 1)
            bookIndex > 0 -> open(bookIndex - 1, repo.books[bookIndex - 1].chapterCount)
        }
    }

    /** Re-reads highlights after the verse menu changed something. */
    fun refreshMarks() {
        if (bookIndex == -1 || loaded.isEmpty()) return
        _state.value = buildState(bookIndex, chapter, loaded, focusVerse, scroll = false)
    }

    private fun buildState(
        b: Int, c: Int, verses: List<ChapterVerse>, focus: Int, scroll: Boolean
    ): ReaderState {
        val book = repo.books[b]
        val rows = ArrayList<ReaderRow>(verses.size + 1)
        rows += ReaderRow.Heading("${book.name} $c")
        for (v in verses) {
            rows += ReaderRow.VerseItem(
                number = v.number,
                text = v.text,
                highlight = prefs.highlightOf(ReaderPrefs.verseKey(b, c, v.number)),
                focused = focus > 0 && v.number == focus
            )
        }
        val scrollRow = when {
            !scroll -> -1
            focus > 0 -> rows.indexOfFirst { it is ReaderRow.VerseItem && it.number == focus }.coerceAtLeast(0)
            else -> 0
        }
        return ReaderState(
            bookName = book.name,
            translation = repo.translation,
            rows = rows,
            hasPrevious = b > 0 || c > 1,
            hasNext = b < repo.books.lastIndex || c < book.chapterCount,
            totalChapters = book.chapterCount,
            scrollToRow = scrollRow
        )
    }

    override fun onCleared() {
        executor.shutdown()
    }
}
