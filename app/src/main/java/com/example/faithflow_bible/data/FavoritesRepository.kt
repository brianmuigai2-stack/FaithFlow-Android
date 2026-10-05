package com.example.faithflow_bible.data

import android.content.Context

data class FavoriteVerse(
    val bookIndex: Int,
    val chapter: Int,
    val verse: Int,
    val reference: String,
    val text: String,
    val highlight: Int,
    val saved: Boolean
)

data class FavoriteStats(val highlighted: Int, val saved: Int, val chapters: Int)

fun List<FavoriteVerse>.stats() = FavoriteStats(
    highlighted = count { it.highlight >= 0 },
    saved = count { it.saved },
    chapters = map { it.bookIndex to it.chapter }.distinct().size
)

/** Turns the reader's highlights and saves into verses with their text. */
class FavoritesRepository(context: Context) {

    private val bible = BibleRepository.get(context)
    private val prefs = ReaderPrefs(context)

    /** Blocking, so call it off the main thread. */
    fun load(): List<FavoriteVerse> {
        val chapterCache = HashMap<Pair<Int, Int>, Map<Int, String>>()
        return prefs.allMarks().mapNotNull { mark ->
            val book = bible.books.getOrNull(mark.book) ?: return@mapNotNull null
            if (mark.chapter < 1 || mark.chapter > book.chapterCount) return@mapNotNull null
            val verses = chapterCache.getOrPut(mark.book to mark.chapter) {
                bible.loadChapter(mark.book, mark.chapter).associate { it.number to it.text }
            }
            val text = verses[mark.verse] ?: return@mapNotNull null
            FavoriteVerse(
                bookIndex = mark.book,
                chapter = mark.chapter,
                verse = mark.verse,
                reference = "${book.name} ${mark.chapter}:${mark.verse}",
                text = text.replace("\n", " "),
                highlight = mark.highlight,
                saved = mark.saved
            )
        }
    }
}