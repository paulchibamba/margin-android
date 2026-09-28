package com.paulchibamba.margin.feature.reminder

import java.time.Duration
import java.time.LocalTime
import java.time.ZonedDateTime

object ReminderTime {
    val EVENING: LocalTime = LocalTime.of(19, 0)

    fun delayUntilNext(time: LocalTime, now: ZonedDateTime): Duration {
        val todayAtTime = now.with(time)
        val next = if (todayAtTime.isAfter(now)) todayAtTime else todayAtTime.plusDays(1)
        return Duration.between(now, next)
    }
}
