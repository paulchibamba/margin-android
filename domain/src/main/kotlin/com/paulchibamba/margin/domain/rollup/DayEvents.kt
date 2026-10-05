package com.paulchibamba.margin.domain.rollup

import com.paulchibamba.margin.domain.model.NoteId
import com.paulchibamba.margin.domain.model.PostId
import com.paulchibamba.margin.domain.tracking.Event
import com.paulchibamba.margin.domain.tracking.LoggedEvent
import java.time.LocalTime
import java.time.ZoneId
import kotlin.time.Duration

class DayEvents(
    val logged: List<LoggedEvent>,
    private val zone: ZoneId,
    private val noteReadingTimes: Map<NoteId, Duration> = emptyMap(),
) {
    val impressionsByPost: Map<PostId, Event.PostImpression> by lazy {
        all<Event.PostImpression>().associateBy(Event.PostImpression::postId)
    }

    inline fun <reified T : Event> all(): List<T> = logged.map(LoggedEvent::event).filterIsInstance<T>()

    inline fun <reified T : Event> timed(): List<Pair<LoggedEvent, T>> =
        logged.mapNotNull { entry -> (entry.event as? T)?.let { event -> entry to event } }

    fun localTimeOf(entry: LoggedEvent): LocalTime = entry.at.atZone(zone).toLocalTime()

    fun readingTimeOf(note: NoteId): Duration? = noteReadingTimes[note]
}
