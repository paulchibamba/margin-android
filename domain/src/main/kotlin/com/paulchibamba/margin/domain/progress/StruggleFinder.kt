package com.paulchibamba.margin.domain.progress

import com.paulchibamba.margin.domain.actions.PostAction
import com.paulchibamba.margin.domain.memory.Rating
import com.paulchibamba.margin.domain.model.NoteId
import com.paulchibamba.margin.domain.rollup.DayEvents
import com.paulchibamba.margin.domain.tracking.Event
import java.time.Instant

internal object StruggleFinder {
    private const val REOPENED_FROM = 2

    fun of(inputs: StruggleInputs, events: DayEvents, attention: AttentionFacts, since: Instant): List<Struggle> =
        signalsOf(inputs, events, attention, since)
            .mapNotNull { signal -> struggleOf(signal, inputs) }
            .filter { struggle -> inputs.isIntroduced(struggle.concept) }
            .filterNot { struggle -> inputs.hasPassedSince(struggle.concept, struggle.at) }
            .groupBy { struggle -> struggle.concept.id }
            .map { (_, struggles) -> struggles.maxBy(Struggle::at) }

    private fun struggleOf(signal: StruggleSignal, inputs: StruggleInputs): Struggle? =
        inputs.conceptOf(signal.conceptId)?.let { concept -> Struggle(concept, signal.trigger, signal.at) }

    private fun signalsOf(inputs: StruggleInputs, events: DayEvents, attention: AttentionFacts, since: Instant) =
        lostsOf(inputs, since) + againsOf(inputs, since) + rereadsOf(inputs, events) + glancesOf(events, attention)

    private fun lostsOf(inputs: StruggleInputs, since: Instant) = inputs.actions
        .filter { action -> action.action == PostAction.LOST && !action.at.isBefore(since) }
        .map { action -> StruggleSignal(action.conceptId, StruggleTrigger.LOST, action.at) }

    private fun againsOf(inputs: StruggleInputs, since: Instant) = inputs.reviews
        .filter { review -> review.rating == Rating.AGAIN && !review.at.isBefore(since) }
        .map { review -> StruggleSignal(review.conceptId, StruggleTrigger.AGAIN, review.at) }

    private fun rereadsOf(inputs: StruggleInputs, events: DayEvents) = rereadNotesOf(events)
        .flatMap { (note, at) ->
            inputs.conceptsOnNote(note).map { StruggleSignal(it.id, StruggleTrigger.REREAD, at) }
        }

    private fun rereadNotesOf(events: DayEvents): List<Pair<NoteId, Instant>> {
        val reopens = events.timed<Event.NoteOpen>()
            .filter { (_, open) -> open.openCount >= REOPENED_FROM }
            .map { (entry, open) -> open.noteId to entry.at }
        val scrollBacks = events.timed<Event.NoteExposure>()
            .filter { (_, exposure) -> exposure.scrollBacks >= 1 }
            .map { (entry, exposure) -> exposure.noteId to entry.at }
        return reopens + scrollBacks
    }

    private fun glancesOf(events: DayEvents, attention: AttentionFacts) = events.timed<Event.PostExposure>()
        .mapNotNull { (entry, exposure) ->
            val impression = events.impressionsByPost[exposure.postId]
            impression?.let { StruggleSignal(it.conceptId, StruggleTrigger.GLANCE, entry.at) }
        }
        .filter { signal -> signal.conceptId in attention.glancedOnly }
}
