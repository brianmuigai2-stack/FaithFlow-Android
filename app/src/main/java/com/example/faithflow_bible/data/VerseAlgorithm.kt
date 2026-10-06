package com.example.faithflow_bible.data

import java.time.ZonedDateTime

object VerseAlgorithm {
    const val SHUFFLE_SEED: Long = 20240711L

    val shuffledReferences: List<String> by lazy {
        deterministicShuffle(DailyVerseData.references, SHUFFLE_SEED)
    }

    fun deterministicShuffle(references: List<String>, seed: Long = SHUFFLE_SEED): List<String> {
        val shuffled = references.toMutableList()
        val random = StableRandom(seed)
        for (i in shuffled.lastIndex downTo 1) {
            val j = random.nextInt(i + 1)
            val tmp = shuffled[i]
            shuffled[i] = shuffled[j]
            shuffled[j] = tmp
        }
        return shuffled
    }

    fun referenceForHour(
        dateTime: ZonedDateTime = ZonedDateTime.now(),
        hourOffset: Long = 0,
        references: List<String> = shuffledReferences
    ): String {
        require(references.isNotEmpty()) { "Verse reference pool must not be empty." }
        val hourIndex = localHourIndex(dateTime) + hourOffset
        val index = Math.floorMod(hourIndex, references.size.toLong()).toInt()
        return references[index]
    }

    fun localHourIndex(dateTime: ZonedDateTime): Long =
        dateTime.toLocalDate().toEpochDay() * 24L + dateTime.hour

    private class StableRandom(seed: Long) {
        private var state = seed and UINT_MASK

        fun nextInt(bound: Int): Int {
            require(bound > 0) { "Bound must be positive." }
            state = (state * MULTIPLIER + INCREMENT) and UINT_MASK
            return (state % bound).toInt()
        }

        companion object {
            private const val MULTIPLIER = 1664525L
            private const val INCREMENT = 1013904223L
            private const val UINT_MASK = 0xffffffffL
        }
    }
}
