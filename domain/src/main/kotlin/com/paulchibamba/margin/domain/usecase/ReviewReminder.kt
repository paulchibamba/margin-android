package com.paulchibamba.margin.domain.usecase

import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

data class ReviewReminder(val dueCount: Int, val streak: Int) {

    val reviewTime: Duration
        get() = maxOf(1.minutes, (TIME_PER_REVIEW * dueCount).inWholeRoundedMinutes().minutes)

    private fun Duration.inWholeRoundedMinutes(): Long = (inWholeSeconds + HALF_MINUTE_SECONDS) / 60

    private companion object {
        val TIME_PER_REVIEW = 25.seconds
        const val HALF_MINUTE_SECONDS = 30
    }
}
