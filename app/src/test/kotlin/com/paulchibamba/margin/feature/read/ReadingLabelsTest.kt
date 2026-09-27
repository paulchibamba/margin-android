package com.paulchibamba.margin.feature.read

import com.paulchibamba.margin.domain.usecase.NoteTally
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class ReadingLabelsTest {

    @Test
    fun `under an hour the time left is shown in minutes`() {
        assertEquals("38 min", timeLeftLabel(38.minutes))
    }

    @Test
    fun `part of a minute rounds up to a whole minute`() {
        assertEquals("3 min", timeLeftLabel(2.minutes + 10.seconds))
    }

    @Test
    fun `an hour or more is shown in hours and minutes`() {
        assertEquals("2 h 6 m", timeLeftLabel(126.minutes))
    }

    @Test
    fun `whole hours drop the minutes`() {
        assertEquals("4 h", timeLeftLabel(240.minutes))
    }

    @Test
    fun `seconds that round up to the hour show as whole hours`() {
        assertEquals("1 h", timeLeftLabel(59.minutes + 1.seconds))
    }

    @Test
    fun `nothing left is zero minutes`() {
        assertEquals("0 min", timeLeftLabel(0.seconds))
    }

    @Test
    fun `book progress shows notes read and the time left`() {
        assertEquals("204/330 notes · 2 h 6 m left", bookProgressLabel(NoteTally(204, 330, 126.minutes)))
    }

    @Test
    fun `a finished book says all read`() {
        assertEquals("12/12 notes · all read", bookProgressLabel(NoteTally(12, 12, 0.minutes)))
    }

    @Test
    fun `ring labels drop the words that books share`() {
        val titles = listOf("Alice and Bob Learn Application Security", "Alice and Bob Learn Secure Coding")

        assertEquals(listOf("Application Security", "Secure Coding"), ringLabels(titles))
    }

    @Test
    fun `a title with nothing in common keeps its words without a leading the`() {
        val titles = listOf("The Tangled Web", "Practical AI Security")

        assertEquals(listOf("Tangled Web", "Practical AI Security"), ringLabels(titles))
    }

    @Test
    fun `a title that is the start of another keeps its last word`() {
        assertEquals(listOf("Security", "Engineering"), ringLabels(listOf("Security", "Security Engineering")))
    }
}
