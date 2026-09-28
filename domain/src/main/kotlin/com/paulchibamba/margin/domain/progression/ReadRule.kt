package com.paulchibamba.margin.domain.progression

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class ReadRule(
    private val minimumDwell: Duration = DEFAULT_MINIMUM_DWELL,
    private val shortNoteWords: Int = DEFAULT_SHORT_NOTE_WORDS,
) {
    fun isRead(dwell: Duration, wordCount: Int): Boolean = dwell >= dwellNeeded(wordCount)

    fun dwellNeeded(wordCount: Int): Duration = if (wordCount < shortNoteWords) Duration.ZERO else minimumDwell

    companion object {
        val DEFAULT_MINIMUM_DWELL = 8.seconds
        const val DEFAULT_SHORT_NOTE_WORDS = 60
    }
}
