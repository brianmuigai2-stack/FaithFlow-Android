package com.example.faithflow_bible.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class BookInfo(val index: Int, val name: String, val file: String, val chapterCount: Int)
data class VerseRef(val bookIndex: Int, val chapter: Int, val verse: Int)
data class ChapterVerse(val number: Int, val text: String)

/** Reads the bundled World English Bible from app/src/main/assets/bible. */
class BibleRepository private constructor(context: Context) {

    private val assets = context.applicationContext.assets
    val books: List<BookInfo> = loadIndex()
    private val cache = HashMap<Int, JSONArray>()

    private fun read(path: String): String =
        assets.open(path).bufferedReader().use { it.readText() }

    private fun loadIndex(): List<BookInfo> {
        val arr = JSONArray(read("bible/index.json"))
        return List(arr.length()) { i ->
            val o = arr.getJSONObject(i)
            BookInfo(i, o.getString("name"), o.getString("file"), o.getInt("chapters"))
        }
    }

    /** Blocking (parses a book on first use), so call it off the main thread. */
    @Synchronized
    fun loadChapter(bookIndex: Int, chapter: Int): List<ChapterVerse> {
        val chapters = cache[bookIndex] ?: JSONObject(read("bible/web/" + books[bookIndex].file))
            .getJSONArray("chapters")
            .also {
                if (cache.size >= 3) cache.clear()
                cache[bookIndex] = it
            }
        val verses = chapters.getJSONArray(chapter - 1)
        return List(verses.length()) { i ->
            val v = verses.getJSONArray(i)
            ChapterVerse(v.getInt(0), v.getString(1))
        }
    }

    /** "Psalm 46:10" -> book / chapter / verse, or null if it can't be understood. */
    fun parseReference(reference: String): VerseRef? {
        val m = Regex("""^(.+?)\s+(\d+):(\d+)""").find(reference.trim()) ?: return null
        var name = m.groupValues[1].lowercase()
        if (name == "psalm") name = "psalms"
        if (name == "song of songs") name = "song of solomon"
        val book = books.firstOrNull { it.name.lowercase() == name } ?: return null
        return VerseRef(book.index, m.groupValues[2].toInt(), m.groupValues[3].toInt())
    }

    companion object {
        @Volatile
        private var instance: BibleRepository? = null

        fun get(context: Context): BibleRepository =
            instance ?: synchronized(this) {
                instance ?: BibleRepository(context).also { instance = it }
            }
    }
}
