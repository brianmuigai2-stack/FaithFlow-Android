package com.example.faithflow_bible

import com.example.faithflow_bible.data.BibleVerse
import com.example.faithflow_bible.data.CuratedVerse
import com.example.faithflow_bible.data.DailyVerseData
import com.example.faithflow_bible.data.OfflineBibleSource
import com.example.faithflow_bible.data.VerseAlgorithm
import com.example.faithflow_bible.data.VerseRepository
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class VerseAlgorithmTest {
    @Test
    fun sameSeedAlwaysProducesSameShuffledOrder() {
        val first = VerseAlgorithm.deterministicShuffle(DailyVerseData.references)
        val second = VerseAlgorithm.deterministicShuffle(DailyVerseData.references)

        assertEquals(first, second)
        assertEquals(first, VerseAlgorithm.shuffledReferences)
    }

    @Test
    fun sameLocalHourProducesSameReferenceOnEveryDevice() {
        val time = ZonedDateTime.of(2026, 10, 6, 10, 15, 0, 0, ZoneId.of("Africa/Nairobi"))

        assertEquals(
            VerseAlgorithm.referenceForHour(time),
            VerseAlgorithm.referenceForHour(time)
        )
    }

    @Test
    fun verseDoesNotChangeDuringSameLocalHour() {
        val first = ZonedDateTime.of(2026, 10, 6, 10, 1, 0, 0, ZoneId.systemDefault())
        val second = first.withMinute(59).withSecond(59)

        assertEquals(
            VerseAlgorithm.referenceForHour(first),
            VerseAlgorithm.referenceForHour(second)
        )
    }

    @Test
    fun verseChangesWhenLocalHourChanges() {
        val first = ZonedDateTime.of(2026, 10, 6, 10, 59, 0, 0, ZoneId.systemDefault())
        val second = first.plusMinutes(1)

        assertNotEquals(
            VerseAlgorithm.referenceForHour(first),
            VerseAlgorithm.referenceForHour(second)
        )
    }

    @Test
    fun duplicateReferencesAreRemoved() {
        val rawCount = DailyVerseData.themes.sumOf { it.references.size }

        assertTrue(rawCount > DailyVerseData.references.size)
        assertEquals(DailyVerseData.references.size, DailyVerseData.references.toSet().size)
    }

    @Test
    fun themeLookupReturnsCorrectTheme() {
        assertEquals("Faith", DailyVerseData.themeByReference["John 3:16"])
        assertEquals("Strength", DailyVerseData.themeByReference["Philippians 4:13"])
        assertEquals("Love", DailyVerseData.themeByReference["1 Corinthians 13:4"])
    }

    @Test
    fun offlineBibleIsPreferredOverCuratedFallbackData() {
        val repository = VerseRepository(
            offlineBibleSource = FakeBibleSource("John 3:16", "Offline text"),
            curatedFallback = mapOf("John 3:16" to CuratedVerse("John 3:16", "Curated text"))
        )

        assertEquals("Offline text", repository.resolve("John 3:16").text)
    }

    @Test
    fun curatedDataIsUsedWhenOfflineBibleLookupFails() {
        val repository = VerseRepository(
            offlineBibleSource = FakeBibleSource(),
            curatedFallback = mapOf("John 3:16" to CuratedVerse("John 3:16", "Curated text"))
        )

        assertEquals("Curated text", repository.resolve("John 3:16").text)
    }

    @Test
    fun emergencyVerseIsUsedWhenBothSourcesFail() {
        val repository = VerseRepository(
            offlineBibleSource = FakeBibleSource(),
            curatedFallback = emptyMap(),
            emergencyVerse = CuratedVerse("Psalm 119:105", "Emergency text")
        )

        val verse = repository.resolve("John 3:16")

        assertEquals("Psalm 119:105", verse.reference)
        assertEquals("Emergency text", verse.text)
    }

    @Test
    fun indexWrapsWhenHourCountExceedsPoolSize() {
        val references = listOf("A 1:1", "B 1:1", "C 1:1")
        val time = ZonedDateTime.of(1970, 1, 1, 5, 0, 0, 0, ZoneId.systemDefault())

        assertEquals("C 1:1", VerseAlgorithm.referenceForHour(time, references = references))
    }

    @Test
    fun negativeHourOffsetsWorkCorrectly() {
        val time = ZonedDateTime.of(1970, 1, 1, 0, 0, 0, 0, ZoneId.systemDefault())
        val references = listOf("A 1:1", "B 1:1", "C 1:1")

        assertEquals("C 1:1", VerseAlgorithm.referenceForHour(time, hourOffset = -1, references = references))
    }

    @Test
    fun algorithmUsesLocalTimezoneRatherThanUtc() {
        val instant = Instant.parse("2026-10-06T10:15:00Z")
        val utc = instant.atZone(ZoneId.of("UTC"))
        val nairobi = instant.atZone(ZoneId.of("Africa/Nairobi"))

        assertEquals(10, utc.hour)
        assertEquals(13, nairobi.hour)
        assertNotEquals(VerseAlgorithm.localHourIndex(utc), VerseAlgorithm.localHourIndex(nairobi))
    }

    @Test
    fun versePoolHasExpectedProductionScale() {
        assertTrue(DailyVerseData.themes.size in 25..30)
        assertFalse(DailyVerseData.references.isEmpty())
        assertTrue(DailyVerseData.references.size >= 250)
    }

    private class FakeBibleSource(
        private val matchingReference: String? = null,
        private val text: String = ""
    ) : OfflineBibleSource {
        override fun getVerseByReference(reference: String): BibleVerse? {
            if (reference != matchingReference) return null
            val parsed = VerseRepository.parseReference(reference) ?: return null
            return BibleVerse(
                reference = reference,
                book = parsed.book,
                chapter = parsed.chapter,
                verse = parsed.verse,
                text = text,
                translation = "Fake",
                abbreviation = "FAKE",
                theme = DailyVerseData.themeByReference[reference].orEmpty()
            )
        }
    }
}
