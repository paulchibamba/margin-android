package com.paulchibamba.margin.feature.debug

import kotlin.time.Duration

data class DebugToolsUiState(
    val offset: Duration = Duration.ZERO,
    val dueCount: Int? = null,
    val isProgressResetArmed: Boolean = false,
    val isProgressCleared: Boolean = false,
) {

    val clockLabel: String
        get() = if (offset == Duration.ZERO) "Clock: real time" else "Clock: +$offset"

    val resetProgressLabel: String
        get() = if (isProgressResetArmed) "Tap again to erase all progress" else "Reset progress"

    val dueCountLabel: String?
        get() = when (dueCount) {
            null -> null
            0 -> "Nothing is due"
            1 -> "1 concept is due"
            else -> "$dueCount concepts are due"
        }
}
