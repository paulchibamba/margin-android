package com.paulchibamba.margin.domain.time

import com.paulchibamba.margin.domain.repository.ClockOffsetStore
import com.paulchibamba.margin.domain.usecase.FixedClock
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours

class OffsetClockTest {

    private val base = FixedClock(Instant.parse("2026-10-01T08:00:00Z"))
    private val store = MemoryOffsetStore()

    @Test
    fun `with no stored offset the clock tells the system time`() {
        assertEquals(base.instant, OffsetClock(base, store).now())
    }

    @Test
    fun `advancing adds to the offset`() {
        val clock = OffsetClock(base, store)

        clock.advance(1.hours)
        clock.advance(1.days)

        assertEquals(Instant.parse("2026-10-02T09:00:00Z"), clock.now())
        assertEquals(25.hours, clock.offset)
    }

    @Test
    fun `the offset is stored so a restarted clock keeps it`() {
        OffsetClock(base, store).advance(2.days)

        assertEquals(Instant.parse("2026-10-03T08:00:00Z"), OffsetClock(base, store).now())
    }

    @Test
    fun `resetting returns to the system time and clears the stored offset`() {
        val clock = OffsetClock(base, store)
        clock.advance(1.days)

        clock.reset()

        assertEquals(base.instant, clock.now())
        assertEquals(Duration.ZERO, store.offset)
    }

    @Test
    fun `the zone is the system zone`() {
        assertEquals(base.zone(), OffsetClock(base, store).zone())
    }

    private class MemoryOffsetStore(var offset: Duration = Duration.ZERO) : ClockOffsetStore {
        override fun load(): Duration = offset
        override fun save(offset: Duration) {
            this.offset = offset
        }
    }
}
