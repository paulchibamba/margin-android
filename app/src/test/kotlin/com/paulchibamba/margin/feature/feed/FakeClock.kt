package com.paulchibamba.margin.feature.feed

import com.paulchibamba.margin.domain.repository.Clock
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import kotlin.time.Duration
import kotlin.time.toJavaDuration

class FakeClock(private var instant: Instant = Instant.parse("2026-10-01T08:00:00Z")) : Clock {
    override fun now(): Instant = instant
    override fun zone(): ZoneId = ZoneOffset.UTC

    fun advanceBy(duration: Duration) {
        instant += duration.toJavaDuration()
    }
}
