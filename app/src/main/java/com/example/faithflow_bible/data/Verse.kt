package com.example.faithflow_bible.data

import android.content.Context
import java.util.Calendar

data class Verse(
    val reference: String,
    val translation: String,
    val text: String,
    val tag: String
)

/** Sample picks for Home. The wording comes from the selected translation's bundled text. */
object SampleVerses {

    private val hourlyPool = listOf(
        Verse("Psalm 46:10", "WEB", "Be still, and know that I am God. I will be exalted among the nations. I will be exalted in the earth.", "Peace"),
        Verse("2 Corinthians 5:7", "WEB", "for we walk by faith, not by sight.", "Faith"),
        Verse("Philippians 4:13", "WEB", "I can do all things through Christ, who strengthens me.", "Strength"),
        Verse("Proverbs 3:5", "WEB", "Trust in Yahweh with all your heart, and don't lean on your own understanding.", "Trust"),
        Verse("Matthew 11:28", "WEB", "Come to me, all you who labor and are heavily burdened, and I will give you rest.", "Rest"),
        Verse("Psalm 119:105", "WEB", "Your word is a lamp to my feet, and a light for my path.", "Guidance"),
        Verse("Isaiah 41:10", "WEB", "Don't you be afraid, for I am with you. Don't be dismayed, for I am your God. I will strengthen you. Yes, I will help you. Yes, I will uphold you with the right hand of my righteousness.", "Courage"),
        Verse("John 3:16", "WEB", "For God so loved the world, that he gave his one and only Son, that whoever believes in him should not perish, but have eternal life.", "Love")
    )

    /** Changes every hour, and cycles through the pool. Uses the current translation. */
    fun verseOfTheHour(context: Context): Verse {
        val repo = BibleRepository.get(context)
        val translation = repo.translation
        val now = Calendar.getInstance()
        val slot = now.get(Calendar.DAY_OF_YEAR) * 24 + now.get(Calendar.HOUR_OF_DAY)
        val base = hourlyPool[slot % hourlyPool.size]
        return try {
            val ref = repo.parseReference(base.reference)
            ref?.let { verseRef ->
                val verses = repo.loadChapter(verseRef.bookIndex, verseRef.chapter, translation)
                val text = verses.firstOrNull { it.number == verseRef.verse }?.text ?: base.text
                base.copy(translation = translation.code, text = text)
            } ?: base
        } catch (e: Exception) {
            base
        }
    }
}