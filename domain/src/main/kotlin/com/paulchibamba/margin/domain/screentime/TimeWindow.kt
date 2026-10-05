package com.paulchibamba.margin.domain.screentime

import java.time.Instant
import kotlin.time.Duration
import kotlin.time.Duration.Companion.ZERO
import kotlin.time.toKotlinDuration

data class TimeWindow(val start: Instant, val end: Instant) {

    fun overlapOf(from: Instant, until: Instant): Duration {
        val overlapStart = maxOf(from, start)
        val overlapEnd = minOf(until, end)
        return if (overlapEnd.isAfter(overlapStart)) {
            java.time.Duration.between(overlapStart, overlapEnd).toKotlinDuration()
        } else {
            ZERO
        }
    }
}
