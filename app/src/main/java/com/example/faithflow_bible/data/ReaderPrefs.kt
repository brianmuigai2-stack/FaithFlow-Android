package com.example.faithflow_bible.data

import android.content.Context

/** Highlight colours, in the order they appear in the verse menu. */
object HighlightColors {
    val swatches = intArrayOf(
        0xFFF2B84B.toInt(), // gold
        0xFF1FA39A.toInt(), // teal
        0xFF4C7FD8.toInt(), // blue
        0xFFE8628A.toInt(), // pink
        0xFF8E6BD8.toInt()  // purple
    )
}

/** One verse the reader has highlighted and/or saved. */
data class VerseMark(
    val book: Int,
    val chapter: Int,
    val verse: Int,
    val highlight: Int,
    val saved: Boolean,
    val time: Long
)

/** Tiny on-device storage for reading settings, highlights and saved verses. */
class ReaderPrefs(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences("reader", Context.MODE_PRIVATE)

    var textSizeIndex: Int
        get() = prefs.getInt("text_size", 1)
        set(value) {
            prefs.edit().putInt("text_size", value).apply()
        }

    /** Index into [HighlightColors.swatches], or -1 when the verse isn't highlighted. */
    fun highlightOf(key: String): Int = prefs.getInt("hl:$key", -1)

    fun setHighlight(key: String, color: Int) {
        val editor = prefs.edit()
        if (color < 0) {
            editor.remove("hl:$key").remove("hlt:$key")
        } else {
            editor.putInt("hl:$key", color).putLong("hlt:$key", System.currentTimeMillis())
        }
        editor.apply()
    }

    fun isSaved(key: String): Boolean = prefs.contains("saved:$key")

    /** Returns true if the verse is saved after the toggle. */
    fun toggleSaved(key: String): Boolean {
        val nowSaved = !isSaved(key)
        val editor = prefs.edit()
        if (nowSaved) editor.putLong("saved:$key", System.currentTimeMillis()) else editor.remove("saved:$key")
        editor.apply()
        return nowSaved
    }

    /** Every highlighted or saved verse, newest first. */
    fun allMarks(): List<VerseMark> {
        class Acc(var highlight: Int = -1, var saved: Boolean = false, var time: Long = 0L)

        val found = LinkedHashMap<String, Acc>()
        for ((name, value) in prefs.all) {
            val prefix = name.substringBefore(':')
            if (prefix != "hl" && prefix != "saved" && prefix != "hlt") continue
            val acc = found.getOrPut(name.substringAfter(':')) { Acc() }
            when (prefix) {
                "hl" -> acc.highlight = (value as? Int) ?: -1
                "saved" -> {
                    acc.saved = true
                    acc.time = maxOf(acc.time, (value as? Long) ?: 0L)
                }
                "hlt" -> acc.time = maxOf(acc.time, (value as? Long) ?: 0L)
            }
        }
        return found.mapNotNull { (key, acc) ->
            if (acc.highlight < 0 && !acc.saved) return@mapNotNull null
            val parts = key.split(':')
            if (parts.size != 3) return@mapNotNull null
            val book = parts[0].toIntOrNull() ?: return@mapNotNull null
            val chapter = parts[1].toIntOrNull() ?: return@mapNotNull null
            val verse = parts[2].toIntOrNull() ?: return@mapNotNull null
            VerseMark(book, chapter, verse, acc.highlight, acc.saved, acc.time)
        }.sortedWith(
            compareByDescending<VerseMark> { it.time }
                .thenBy { it.book }.thenBy { it.chapter }.thenBy { it.verse }
        )
    }

    companion object {
        fun verseKey(book: Int, chapter: Int, verse: Int) = "$book:$chapter:$verse"
    }
}