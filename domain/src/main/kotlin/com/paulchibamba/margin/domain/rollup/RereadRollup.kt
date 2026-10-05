package com.paulchibamba.margin.domain.rollup

import com.paulchibamba.margin.domain.tracking.Event

object RereadRollup {
    const val HOTSPOTS = 5
    const val REOPENED_FROM = 2

    fun of(day: DayEvents) = RereadMetrics(
        revisits = day.all<Event.PostRevisit>().size,
        notesReopened = reopensOf(day).size,
        scrollBacks = day.all<Event.NoteExposure>().sumOf(Event.NoteExposure::scrollBacks),
        hotspots = hotspotsOf(day),
    )

    private fun hotspotsOf(day: DayEvents): List<RereadHotspot> =
        (revisitedConcepts(day) + reopenedNotes(day) + scrolledBackNotes(day) + rezoomedSubjects(day))
            .groupingBy { it }
            .eachCount()
            .byCountDescending()
            .entries
            .take(HOTSPOTS)
            .map { (subject, count) -> RereadHotspot(subject, count) }

    private fun revisitedConcepts(day: DayEvents): List<RereadSubject> = day.all<Event.PostRevisit>()
        .mapNotNull { revisit -> day.impressionsByPost[revisit.postId]?.conceptId }
        .map(RereadSubject::OfConcept)

    private fun reopenedNotes(day: DayEvents): List<RereadSubject> =
        reopensOf(day).map { open -> RereadSubject.OfNote(open.noteId) }

    private fun scrolledBackNotes(day: DayEvents): List<RereadSubject> = day.all<Event.NoteExposure>()
        .flatMap { exposure -> List(exposure.scrollBacks) { RereadSubject.OfNote(exposure.noteId) } }

    private fun rezoomedSubjects(day: DayEvents): List<RereadSubject> = day.all<Event.ImageZoom>()
        .mapNotNull { zoom -> subjectOf(day, zoom) }
        .groupBy { it }
        .flatMap { (_, zooms) -> zooms.drop(1) }

    private fun subjectOf(day: DayEvents, zoom: Event.ImageZoom): RereadSubject? {
        zoom.noteId?.let { return RereadSubject.OfNote(it) }
        return zoom.postId?.let(day.impressionsByPost::get)?.let { RereadSubject.OfConcept(it.conceptId) }
    }

    private fun reopensOf(day: DayEvents): List<Event.NoteOpen> =
        day.all<Event.NoteOpen>().filter { it.openCount >= REOPENED_FROM }
}
