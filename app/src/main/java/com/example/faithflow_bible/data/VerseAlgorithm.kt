package com.example.faithflow_bible.data

import java.time.ZonedDateTime

object VerseAlgorithm {
    // 64-bit golden ratio (Knuth's multiplicative hash constant) as signed Long
    // 0x9E3779B97F4A7C15 = -7046029254386353131L (signed 64-bit)
    private const val KNUTH_HASH_CONSTANT = -7046029254386353131L

    fun referenceForHour(
        dateTime: ZonedDateTime = ZonedDateTime.now(),
        hourOffset: Long = 0,
        references: List<String> = DailyVerseData.references
    ): String {
        require(references.isNotEmpty()) { "Verse reference pool must not be empty." }
        val hourIndex = localHourIndex(dateTime) + hourOffset
        val index = hashHourIndexToVerseIndex(hourIndex, references.size)
        return references[index]
    }

    fun localHourIndex(dateTime: ZonedDateTime): Long =
        dateTime.toLocalDate().toEpochDay() * 24L + dateTime.hour

    private fun hashHourIndexToVerseIndex(hourIndex: Long, poolSize: Int): Int {
        // Knuth's multiplicative hash: use high 32 bits of 64-bit product
        val hash = (hourIndex.toULong() * KNUTH_HASH_CONSTANT.toULong()).shr(32).toLong()
        return Math.floorMod(hash, poolSize.toLong()).toInt()
    }

    @Deprecated("Use referenceForHour with hash-based algorithm", ReplaceWith("referenceForHour(dateTime, hourOffset, references)"))
    fun deterministicShuffle(references: List<String>, seed: Long = 20240711L): List<String> {
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
