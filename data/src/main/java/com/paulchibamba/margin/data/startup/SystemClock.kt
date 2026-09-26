package com.paulchibamba.margin.data.startup

import com.paulchibamba.margin.domain.repository.Clock
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit

class SystemClock : Clock {
    override fun now(): Instant = Instant.now().truncatedTo(ChronoUnit.MILLIS)

    override fun zone(): ZoneId = ZoneId.systemDefault()
}
