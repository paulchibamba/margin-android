package com.paulchibamba.margin.domain.memory

import java.time.Duration
import java.time.Instant
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

internal object DayCount {

    fun calendarDaysBetween(from: Instant, to: Instant): Int =
        ChronoUnit.DAYS.between(from.utcDate(), to.utcDate()).toInt()

    fun wholeDaysBetween(from: Instant, to: Instant): Int =
        Math.floorDiv(Duration.between(from, to).toMillis(), MILLIS_PER_DAY).toInt()

    private fun Instant.utcDate() = atZone(ZoneOffset.UTC).toLocalDate()

    private const val MILLIS_PER_DAY = 86_400_000L
}
