package com.paulchibamba.margin.domain.progress

import com.paulchibamba.margin.domain.model.Concept
import com.paulchibamba.margin.domain.model.ConceptId
import com.paulchibamba.margin.domain.rollup.DayEvents
import com.paulchibamba.margin.domain.rollup.RereadRollup
import com.paulchibamba.margin.domain.rollup.RereadSubject
import com.paulchibamba.margin.domain.tracking.Event
import kotlin.time.Duration.Companion.milliseconds

internal object AttentionFactsCalculator {
    private const val HOTSPOT_REREADS = 2
    private const val GLANCE_IMPRESSIONS = 3
    private val GLANCE_LIMIT = 1500.milliseconds

    fun of(context: ProgressContext, events: DayEvents): AttentionFacts =
        AttentionFacts(hotspots = hotspotsOf(context, events), glancedOnly = glancedOnlyOf(events))

    private fun exposuresByConcept(events: DayEvents): Map<ConceptId, List<Event.PostExposure>> = events
        .all<Event.PostExposure>()
        .mapNotNull { exposure -> events.impressionsByPost[exposure.postId]?.let { it.conceptId to exposure } }
        .groupBy(keySelector = { it.first }, valueTransform = { it.second })

    private fun hotspotsOf(context: ProgressContext, events: DayEvents): List<ConceptHotspot> =
        RereadRollup.subjectsOf(events)
            .flatMap { subject -> conceptsOf(subject, context) }
            .groupingBy { concept -> concept }
            .eachCount()
            .filterValues { rereads -> rereads >= HOTSPOT_REREADS }
            .map { (concept, rereads) -> ConceptHotspot(concept, rereads) }
            .sortedByDescending(ConceptHotspot::rereads)

    private fun conceptsOf(subject: RereadSubject, context: ProgressContext): List<Concept> = when (subject) {
        is RereadSubject.OfConcept -> listOfNotNull(context.conceptOf(subject.conceptId))
        is RereadSubject.OfNote -> context.conceptsByNote[subject.noteId].orEmpty()
    }

    private fun glancedOnlyOf(events: DayEvents): Set<ConceptId> = exposuresByConcept(events)
        .filterValues { exposures -> exposures.size >= GLANCE_IMPRESSIONS && exposures.all(::isGlance) }
        .keys

    private fun isGlance(exposure: Event.PostExposure): Boolean = exposure.activeTime < GLANCE_LIMIT
}
