package com.paulchibamba.margin.domain.tracking

import com.paulchibamba.margin.domain.usecase.FixedClock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

class AttentionTrackerTest {
    private val clock = FixedClock()
    private val tracker = AttentionTracker(clock).apply {
        restart()
        setSettled(true)
    }

    private fun advance(by: Duration) {
        clock.instant = clock.instant + by.toJavaDuration()
    }

    @Test
    fun `a settled post is active for 20 seconds after the last input, then idle`() {
        advance(60.seconds)

        assertEquals(AttentionTotals(active = 20.seconds, idle = 40.seconds), tracker.totals())
    }

    @Test
    fun `a touch makes the next 20 seconds active again`() {
        advance(30.seconds)
        tracker.onInput()
        advance(25.seconds)

        assertEquals(AttentionTotals(active = 40.seconds, idle = 15.seconds), tracker.totals())
    }

    @Test
    fun `time in the background counts as neither active nor idle`() {
        advance(5.seconds)
        tracker.setForeground(false)
        advance(10.minutes())
        tracker.setForeground(true)
        advance(5.seconds)

        assertEquals(AttentionTotals(active = 10.seconds, idle = Duration.ZERO), tracker.totals())
    }

    @Test
    fun `coming back to the app counts as input`() {
        advance(30.seconds)
        tracker.setForeground(false)
        advance(1.seconds)
        tracker.setForeground(true)
        advance(15.seconds)

        assertEquals(AttentionTotals(active = 35.seconds, idle = 10.seconds), tracker.totals())
    }

    @Test
    fun `a screen that is off or a post that is not settled counts as nothing`() {
        tracker.setInteractive(false)
        advance(30.seconds)
        tracker.setInteractive(true)
        tracker.setSettled(false)
        advance(30.seconds)

        assertEquals(AttentionTotals(Duration.ZERO, Duration.ZERO), tracker.totals())
    }

    @Test
    fun `restarting clears the totals`() {
        advance(8.seconds)
        tracker.restart()
        advance(3.seconds)

        assertEquals(AttentionTotals(active = 3.seconds, idle = Duration.ZERO), tracker.totals())
    }

    private fun Int.minutes() = (this * 60).seconds
}
