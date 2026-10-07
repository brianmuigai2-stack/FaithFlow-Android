package com.example.faithflow_bible.share

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.faithflow_bible.data.BibleVerse

/**
 * Compose wrapper for the verse card renderer.
 * Keeps rendering logic separate from UI — this is the only Compose-facing file.
 */
@Composable
fun VerseCardPreview(
    verse: BibleVerse,
    modifier: Modifier = Modifier
) {
    // The renderer is not Composable; this is a placeholder for future
    // Compose-based preview integration. Actual rendering goes through
    // VerseCardRenderer / VerseShareManager.
    Box(modifier = modifier.fillMaxSize())
}
