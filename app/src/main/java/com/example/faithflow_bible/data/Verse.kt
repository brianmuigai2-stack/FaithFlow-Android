package com.example.faithflow_bible.data

import android.content.Context

data class Verse(
    val reference: String,
    val translation: String,
    val text: String,
    val tag: String
)

/** Compatibility facade for Home and notifications. */
object SampleVerses {
    fun verseOfTheHour(context: Context): Verse =
        VerseRepository(context).getVerseOfTheHour().toHomeVerse()
}
