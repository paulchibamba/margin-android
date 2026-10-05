package com.paulchibamba.margin.domain.rollup

import com.paulchibamba.margin.domain.tracking.Event
import kotlin.time.Duration

object TimeRollup {

    fun of(day: DayEvents): TimeMetrics {
        val posts = day.all<Event.PostExposure>()
        val notes = day.all<Event.NoteExposure>()
        return TimeMetrics(
            active = posts.sumOf(Event.PostExposure::activeTime) + notes.sumOf(Event.NoteExposure::activeTime),
            idle = posts.sumOf(Event.PostExposure::idleTime) + notes.sumOf(Event.NoteExposure::idleTime),
        )
    }

    private fun <T> List<T>.sumOf(duration: (T) -> Duration): Duration = fold(Duration.ZERO) { sum, item ->
        sum + duration(item)
    }
}
