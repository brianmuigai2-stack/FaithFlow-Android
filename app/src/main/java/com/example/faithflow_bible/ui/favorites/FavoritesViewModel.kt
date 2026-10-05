package com.example.faithflow_bible.ui.favorites

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.faithflow_bible.data.FavoriteVerse
import com.example.faithflow_bible.data.FavoritesRepository
import com.example.faithflow_bible.data.stats
import java.util.concurrent.Executors

enum class FavoritesFilter { ALL, HIGHLIGHTED, SAVED }

data class FavoritesUi(
    val items: List<FavoriteVerse>,
    val highlighted: Int,
    val saved: Int,
    val chapters: Int,
    val filter: FavoritesFilter
)

class FavoritesViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = FavoritesRepository(app)
    private val executor = Executors.newSingleThreadExecutor()

    private val _ui = MutableLiveData<FavoritesUi>()
    val ui: LiveData<FavoritesUi> = _ui

    @Volatile
    private var all: List<FavoriteVerse> = emptyList()

    @Volatile
    private var filter = FavoritesFilter.ALL

    fun refresh() {
        executor.execute {
            all = repo.load()
            _ui.postValue(build())
        }
    }

    /** Tapping the active card again clears the filter. */
    fun toggleFilter(target: FavoritesFilter) {
        filter = if (filter == target) FavoritesFilter.ALL else target
        _ui.value = build()
    }

    private fun build(): FavoritesUi {
        val stats = all.stats()
        val shown = when (filter) {
            FavoritesFilter.ALL -> all
            FavoritesFilter.HIGHLIGHTED -> all.filter { it.highlight >= 0 }
            FavoritesFilter.SAVED -> all.filter { it.saved }
        }
        return FavoritesUi(shown, stats.highlighted, stats.saved, stats.chapters, filter)
    }

    override fun onCleared() {
        executor.shutdown()
    }
}