package com.paulchibamba.margin.domain.rollup

import com.paulchibamba.margin.domain.model.Format
import com.paulchibamba.margin.domain.tracking.Event
import kotlin.time.Duration.Companion.milliseconds

object SessionRollup {

    fun of(day: DayEvents): SessionMetrics {
        val starts = day.all<Event.SessionStart>()
        val durations = day.all<Event.SessionEnd>().map { it.duration.inWholeMilliseconds.toDouble() }
        return SessionMetrics(
            sessions = starts.size,
            medianSession = Median.of(durations)?.milliseconds,
            entries = starts.groupingBy(Event.SessionStart::entry).eachCount(),
            exitFormats = exitFormatsOf(day),
        )
    }

    private fun exitFormatsOf(day: DayEvents): Map<Format, Int> = day.all<Event.PostExposure>()
        .filter(Event.PostExposure::exitedSession)
        .mapNotNull { exposure -> day.impressionsByPost[exposure.postId]?.format }
        .groupingBy { it }
        .eachCount()
        .byCountDescending()
}
