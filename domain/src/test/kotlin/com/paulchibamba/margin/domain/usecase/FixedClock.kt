package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.repository.Clock
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset

class FixedClock(var instant: Instant = Instant.parse("2026-10-01T08:00:00Z")) : Clock {
    override fun now(): Instant = instant
    override fun zone(): ZoneId = ZoneOffset.UTC
}
