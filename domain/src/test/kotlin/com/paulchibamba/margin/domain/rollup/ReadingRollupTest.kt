package com.paulchibamba.margin.domain.rollup

import com.paulchibamba.margin.domain.model.BookSlug
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.seconds

class ReadingRollupTest {
    private val reading = ReadingRollup.of(RollupFixture.day)

    @Test
    fun `notes read are the notes marked read`() {
        assertEquals(1, reading.notesRead)
    }

    @Test
    fun `pace per book ignores a glance at a note`() {
        val expected = mapOf(
            BookSlug("appsec") to ReadingPace(200, 60.seconds),
            BookSlug("grokking") to ReadingPace(150, 30.seconds),
        )

        assertEquals(expected, reading.paceByBook)
        assertEquals(listOf(200, 300), reading.paceByBook.values.map { it.wordsPerMinute })
    }

    @Test
    fun `pace is grouped by the time of day the note closed`() {
        val expected = mapOf(
            TimeOfDay.MORNING to ReadingPace(200, 60.seconds),
            TimeOfDay.EVENING to ReadingPace(150, 30.seconds),
        )

        assertEquals(expected, reading.paceByTimeOfDay)
    }
}
