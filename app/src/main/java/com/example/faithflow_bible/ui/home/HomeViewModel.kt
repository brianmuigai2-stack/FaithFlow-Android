package com.example.faithflow_bible.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.faithflow_bible.data.FavoriteVerse
import com.example.faithflow_bible.data.FavoritesRepository
import com.example.faithflow_bible.data.SampleVerses
import com.example.faithflow_bible.data.Verse
import com.example.faithflow_bible.data.stats
import java.util.concurrent.Executors

data class HomeUi(
    val saved: Int,
    val highlighted: Int,
    val chapters: Int,
    val recent: List<FavoriteVerse>
)

class HomeViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = FavoritesRepository(app)
    private val executor = Executors.newSingleThreadExecutor()

    private val _ui = MutableLiveData<HomeUi>()
    val ui: LiveData<HomeUi> = _ui

    fun verseOfTheHour(): Verse = SampleVerses.verseOfTheHour()

    /** Re-reads the reader's highlights and saves. */
    fun refresh() {
        executor.execute {
            val all = repo.load()
            val stats = all.stats()
            _ui.postValue(HomeUi(stats.saved, stats.highlighted, stats.chapters, all.take(3)))
        }
    }

    override fun onCleared() {
        executor.shutdown()
    }
}