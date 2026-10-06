package com.paulchibamba.margin.domain.drop

import java.time.LocalTime

data class LearnedSlot(val start: LocalTime, val sessionsInSlot: Int, val sessionsCounted: Int) {
    val isFallback: Boolean get() = sessionsCounted < DropTime.MINIMUM_SESSIONS
}
