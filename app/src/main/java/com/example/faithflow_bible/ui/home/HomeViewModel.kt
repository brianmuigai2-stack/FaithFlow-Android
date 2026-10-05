package com.example.faithflow_bible.ui.home

import androidx.lifecycle.ViewModel
import com.example.faithflow_bible.data.SampleVerses
import com.example.faithflow_bible.data.Verse

class HomeViewModel : ViewModel() {

    // Static for now; these will come from the database once saving/highlighting exists.
    val savedCount = 12
    val highlightCount = 5
    val chaptersRead = 3

    val recentSaved: List<Verse> = SampleVerses.recentSaved

    fun verseOfTheHour(): Verse = SampleVerses.verseOfTheHour()
}
