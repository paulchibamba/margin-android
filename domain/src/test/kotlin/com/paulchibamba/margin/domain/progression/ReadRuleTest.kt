package com.paulchibamba.margin.domain.progression

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class ReadRuleTest {

    private val rule = ReadRule()

    @Test
    fun `a note counts as read after eight seconds`() {
        assertTrue(rule.isRead(dwell = 8.seconds, wordCount = 200))
    }

    @Test
    fun `a longer note left early is not read`() {
        assertFalse(rule.isRead(dwell = 7.seconds, wordCount = 200))
    }

    @Test
    fun `a note under sixty words counts as read at once`() {
        assertTrue(rule.isRead(dwell = Duration.ZERO, wordCount = 59))
        assertEquals(Duration.ZERO, rule.dwellNeeded(wordCount = 59))
    }

    @Test
    fun `a note of exactly sixty words needs the full dwell`() {
        assertEquals(8.seconds, rule.dwellNeeded(wordCount = 60))
    }
}
