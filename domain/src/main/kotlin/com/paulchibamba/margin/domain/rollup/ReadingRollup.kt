package com.paulchibamba.margin.domain.rollup

import com.paulchibamba.margin.domain.tracking.Event
import com.paulchibamba.margin.domain.tracking.LoggedEvent
import kotlin.time.Duration

object ReadingRollup {
    const val MIN_SHARE_OF_EXPECTED = 0.5

    fun of(day: DayEvents): ReadingMetrics {
        val reads = day.timed<Event.NoteExposure>().filter { (_, exposure) -> isRead(day, exposure) }
        return ReadingMetrics(
            notesRead = notesMarkedRead(day),
            paceByBook = paceBy(reads) { _, exposure -> exposure.noteId.bookSlug },
            paceByTimeOfDay = paceBy(reads) { entry, _ -> TimeOfDay.of(day.localTimeOf(entry)) },
        )
    }

    private fun notesMarkedRead(day: DayEvents): Int =
        day.all<Event.NoteExposure>().filter(Event.NoteExposure::isMarkedRead).map { it.noteId }.distinct().size

    private fun isRead(day: DayEvents, exposure: Event.NoteExposure): Boolean {
        if (exposure.activeTime <= Duration.ZERO) return false
        if (exposure.isMarkedRead) return true
        val expected = day.readingTimeOf(exposure.noteId) ?: return false
        return exposure.activeTime >= expected * MIN_SHARE_OF_EXPECTED
    }

    private fun <K> paceBy(
        reads: List<Pair<LoggedEvent, Event.NoteExposure>>,
        key: (LoggedEvent, Event.NoteExposure) -> K,
    ): Map<K, ReadingPace> = reads
        .groupBy({ (entry, exposure) -> key(entry, exposure) }, { (_, exposure) -> paceOf(exposure) })
        .mapValues { (_, paces) -> paces.reduce(ReadingPace::plus) }

    private fun paceOf(exposure: Event.NoteExposure) = ReadingPace(exposure.words, exposure.activeTime)
}
