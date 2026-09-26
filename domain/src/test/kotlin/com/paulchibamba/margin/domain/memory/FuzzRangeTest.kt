package com.paulchibamba.margin.domain.memory

import kotlin.test.Test
import kotlin.test.assertEquals

class FuzzRangeTest {

    @Test
    fun `a ten day interval can land between eight and twelve days`() {
        assertEquals(FuzzRange(shortest = 8, longest = 12), FuzzRange.of(10, elapsedDays = 0, maximumInterval = 36500))
    }

    @Test
    fun `a fuzzed interval is always longer than the time already elapsed`() {
        val range = FuzzRange.of(10, elapsedDays = 9, maximumInterval = 36500)

        assertEquals(10, range.shortest)
    }

    @Test
    fun `a fuzzed interval never passes the maximum interval`() {
        assertEquals(100, FuzzRange.of(100, elapsedDays = 0, maximumInterval = 100).longest)
    }

    @Test
    fun `the fuzz factor spreads evenly from shortest to longest`() {
        val range = FuzzRange(shortest = 8, longest = 12)

        assertEquals(8, range.pick(0.0))
        assertEquals(12, range.pick(0.9999))
    }
}
