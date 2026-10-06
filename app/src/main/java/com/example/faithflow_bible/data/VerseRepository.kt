package com.example.faithflow_bible.data

import android.content.Context
import java.time.ZoneId
import java.time.ZonedDateTime

data class ParsedVerseReference(
    val book: String,
    val chapter: Int,
    val verse: Int
)

interface OfflineBibleSource {
    fun getVerseByReference(reference: String): BibleVerse?
}

class AndroidOfflineBibleSource(context: Context) : OfflineBibleSource {
    private val repository = BibleRepository.get(context.applicationContext)

    override fun getVerseByReference(reference: String): BibleVerse? {
        val parsed = repository.parseReference(reference) ?: return null
        val book = repository.book(parsed.bookIndex) ?: return null
        val translation = repository.translation
        val text = repository.loadChapter(parsed.bookIndex, parsed.chapter, translation)
            .firstOrNull { it.number == parsed.verse }
            ?.text
            ?: return null
        return BibleVerse(
            reference = reference,
            book = book.name,
            chapter = parsed.chapter,
            verse = parsed.verse,
            text = text,
            translation = translation.label,
            abbreviation = translation.code,
            theme = DailyVerseData.themeByReference[reference].orEmpty()
        )
    }
}

class VerseRepository(
    private val offlineBibleSource: OfflineBibleSource?,
    private val curatedFallback: Map<String, CuratedVerse> = DailyVerseData.curatedFallback,
    private val emergencyVerse: CuratedVerse = DailyVerseData.emergencyVerse
) {
    constructor(context: Context) : this(AndroidOfflineBibleSource(context))

    fun getVerseOfTheHour(
        dateTime: ZonedDateTime = ZonedDateTime.now(ZoneId.systemDefault()),
        hourOffset: Long = 0
    ): BibleVerse {
        val reference = VerseAlgorithm.referenceForHour(dateTime, hourOffset)
        return resolve(reference)
    }

    suspend fun getVerseOfTheHourAsync(
        dateTime: ZonedDateTime = ZonedDateTime.now(ZoneId.systemDefault()),
        hourOffset: Long = 0
    ): BibleVerse = getVerseOfTheHour(dateTime, hourOffset)

    fun resolve(reference: String): BibleVerse =
        offlineBibleSource
            ?.runCatching { getVerseByReference(reference) }
            ?.getOrNull()
            ?: curatedFallback[reference]?.toBibleVerse(DailyVerseData.themeByReference[reference].orEmpty())
            ?: emergencyVerse.toBibleVerse(DailyVerseData.themeByReference[emergencyVerse.reference].orEmpty())

    private fun CuratedVerse.toBibleVerse(theme: String): BibleVerse {
        val parsed = parseReference(reference)
        return BibleVerse(
            reference = reference,
            book = parsed?.book.orEmpty(),
            chapter = parsed?.chapter ?: 0,
            verse = parsed?.verse ?: 0,
            text = text,
            translation = translation,
            abbreviation = abbreviation,
            theme = theme
        )
    }

    companion object {
        fun parseReference(reference: String): ParsedVerseReference? {
            val match = Regex("""^(.+?)\s+(\d+):(\d+)$""").find(reference.trim()) ?: return null
            return ParsedVerseReference(
                book = match.groupValues[1],
                chapter = match.groupValues[2].toInt(),
                verse = match.groupValues[3].toInt()
            )
        }
    }
}

fun BibleVerse.toHomeVerse(): Verse =
    Verse(
        reference = reference,
        translation = abbreviation,
        text = text,
        tag = theme
    )
