package com.paulchibamba.margin.domain.rollup

import kotlin.time.Duration

data class ReadingPace(val words: Int, val activeTime: Duration) {

    val wordsPerMinute: Int?
        get() {
            val milliseconds = activeTime.inWholeMilliseconds
            return if (milliseconds <= 0) null else (words * MS_PER_MINUTE / milliseconds).toInt()
        }

    operator fun plus(other: ReadingPace) = ReadingPace(words + other.words, activeTime + other.activeTime)

    private companion object {
        const val MS_PER_MINUTE = 60_000L
    }
}
