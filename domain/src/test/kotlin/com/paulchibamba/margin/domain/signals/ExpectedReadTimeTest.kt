package com.paulchibamba.margin.domain.signals

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.seconds

class ExpectedReadTimeTest {

    @Test
    fun `each word takes a quarter of a second to read`() {
        assertEquals(5.seconds, ExpectedReadTime.of(tipWithWords(20)))
    }

    @Test
    fun `even a very short post is given two and a half seconds`() {
        assertEquals(2.5.seconds, ExpectedReadTime.of(tipWithWords(3)))
    }

    @Test
    fun `even a very long post is expected to take at most twelve seconds`() {
        assertEquals(12.seconds, ExpectedReadTime.of(tipWithWords(200)))
    }
}
