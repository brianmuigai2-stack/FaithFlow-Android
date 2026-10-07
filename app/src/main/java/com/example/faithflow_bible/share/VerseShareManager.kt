package com.example.faithflow_bible.share

import android.content.Context
import android.content.Intent
import com.example.faithflow_bible.data.BibleVerse

class VerseShareManager(private val context: Context) {

    private val renderer = VerseCardRenderer(context)

    suspend fun shareSingleVerse(verse: BibleVerse) {
        val bitmap = renderer.generateVerseCard(verse) ?: return
        val uri = VerseCardExporter.exportForShare(context, bitmap) ?: return
        val text = buildShareText(listOf(verse))
        launchShareSheet(uri, text)
    }

    suspend fun shareVerseCollection(verses: List<BibleVerse>) {
        if (verses.isEmpty()) return
        if (verses.size == 1) { shareSingleVerse(verses.first()); return }
        val bitmap = renderer.generateCollectionCard(verses) ?: return
        val uri = VerseCardExporter.exportForShare(context, bitmap) ?: return
        val text = buildShareText(verses)
        launchShareSheet(uri, text)
    }

    suspend fun saveSingleVerse(verse: BibleVerse): Boolean {
        val bitmap = renderer.generateVerseCard(verse) ?: return false
        return VerseCardExporter.saveToPictures(context, bitmap)
    }

    private fun launchShareSheet(uri: android.net.Uri, text: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_TEXT, text)
            putExtra(Intent.EXTRA_STREAM, uri)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Share Scripture"))
    }

    // Matches web share text format
    private fun buildShareText(verses: List<BibleVerse>): String {
        return if (verses.size == 1) {
            val v = verses.first()
            val url = renderer.buildDeepLink(v)
            "\u201C${v.text}\u201D\n${v.reference} (${v.abbreviation})\n\nRead on FaithFlow: $url"
        } else {
            "My FaithFlow verses (${verses.size})\n\nRead on FaithFlow: ${renderer.buildDeepLink(null)}"
        }
    }
}
