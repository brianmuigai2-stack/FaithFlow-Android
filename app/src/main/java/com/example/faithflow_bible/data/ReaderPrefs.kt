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
        if (color < 0) editor.remove("hl:$key") else editor.putInt("hl:$key", color)
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

    companion object {
        fun verseKey(book: Int, chapter: Int, verse: Int) = "$book:$chapter:$verse"
    }
}
