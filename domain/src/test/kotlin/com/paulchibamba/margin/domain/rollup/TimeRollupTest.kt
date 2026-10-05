package com.paulchibamba.margin.domain.rollup

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class TimeRollupTest {

    @Test
    fun `active and idle time add up across posts and notes`() {
        val time = TimeRollup.of(RollupFixture.day)

        assertEquals(11_600.milliseconds + 95.seconds, time.active)
        assertEquals(12.seconds, time.idle)
    }
}
