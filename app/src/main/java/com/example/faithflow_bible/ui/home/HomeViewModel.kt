package com.example.faithflow_bible.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.faithflow_bible.data.BibleRepository
import com.example.faithflow_bible.data.FavoriteVerse
import com.example.faithflow_bible.data.FavoritesRepository
import com.example.faithflow_bible.data.ReaderPrefs
import com.example.faithflow_bible.data.Verse
import com.example.faithflow_bible.data.VerseRepository
import com.example.faithflow_bible.data.stats
import com.example.faithflow_bible.data.toHomeVerse
import java.util.concurrent.Executors

data class HomeUi(
    val saved: Int,
    val highlighted: Int,
    val chapters: Int,
    val recent: List<FavoriteVerse>,
    val lastReadBook: Int = -1,
    val lastReadChapter: Int = 1,
    val lastReadBookName: String = ""
)

class HomeViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = FavoritesRepository(app)
    private val verseRepository = VerseRepository(app)
    private val prefs = ReaderPrefs(app)
    private val bibleRepo = BibleRepository.get(app)
    private val executor = Executors.newSingleThreadExecutor()

    private val _ui = MutableLiveData<HomeUi>()
    val ui: LiveData<HomeUi> = _ui

    fun verseOfTheHour(): Verse = verseRepository.getVerseOfTheHour().toHomeVerse()

    fun refresh() {
        executor.execute {
            val all = repo.load()
            val stats = all.stats()
            val lastBook = prefs.lastReadBook
            val lastChapter = prefs.lastReadChapter
            val bookName = if (lastBook >= 0) bibleRepo.book(lastBook)?.name ?: "" else ""
            _ui.postValue(HomeUi(
                stats.saved, stats.highlighted, stats.chapters, all.take(4),
                lastBook, lastChapter, bookName
            ))
        }
    }

    override fun onCleared() {
        executor.shutdown()
    }
}
