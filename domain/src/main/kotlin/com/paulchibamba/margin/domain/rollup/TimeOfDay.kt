package com.paulchibamba.margin.domain.rollup

import java.time.LocalTime

enum class TimeOfDay(private val startHour: Int) {
    MORNING(5),
    AFTERNOON(12),
    EVENING(17),
    NIGHT(22);

    companion object {
        fun of(time: LocalTime): TimeOfDay = entries.lastOrNull { time.hour >= it.startHour } ?: NIGHT
    }
}
