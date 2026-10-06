package com.example.faithflow_bible.data

/**
 * Represents a Bible verse with all metadata needed for the Verse of the Hour feature.
 */
data class BibleVerse(
    val reference: String,
    val book: String,
    val chapter: Int,
    val verse: Int,
    val text: String,
    val translation: String,
    val abbreviation: String,
    val theme: String
)