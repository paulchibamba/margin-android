package com.paulchibamba.margin.domain.progress

import com.paulchibamba.margin.domain.actions.PostAction
import com.paulchibamba.margin.domain.memory.Rating
import com.paulchibamba.margin.domain.model.NoteId
import com.paulchibamba.margin.domain.rollup.DayEvents
import com.paulchibamba.margin.domain.tracking.Event
import java.time.Instant

internal object StruggleFinder {
    private const val REOPENED_FROM = 2

    fun of(context: ProgressContext, events: DayEvents, attention: AttentionFacts, since: Instant): List<Struggle> =
        signalsOf(context, events, attention, since)
            .mapNotNull { signal -> struggleOf(signal, context) }
            .filter { struggle -> context.isIntroduced(struggle.concept) }
            .filterNot { struggle -> context.hasPassedSince(struggle.concept, struggle.at) }
            .groupBy { struggle -> struggle.concept.id }
            .map { (_, struggles) -> struggles.maxBy(Struggle::at) }

    private fun struggleOf(signal: StruggleSignal, context: ProgressContext): Struggle? =
        context.conceptOf(signal.conceptId)?.let { concept -> Struggle(concept, signal.trigger, signal.at) }

    private fun signalsOf(context: ProgressContext, events: DayEvents, attention: AttentionFacts, since: Instant) =
        lostsOf(context, since) + againsOf(context, since) + rereadsOf(context, events) + glancesOf(events, attention)

    private fun lostsOf(context: ProgressContext, since: Instant) = context.sources.actions
        .filter { action -> action.action == PostAction.LOST && !action.at.isBefore(since) }
        .map { action -> StruggleSignal(action.conceptId, StruggleTrigger.LOST, action.at) }

    private fun againsOf(context: ProgressContext, since: Instant) = context.sources.reviews
        .filter { review -> review.rating == Rating.AGAIN && !review.at.isBefore(since) }
        .map { review -> StruggleSignal(review.conceptId, StruggleTrigger.AGAIN, review.at) }

    private fun rereadsOf(context: ProgressContext, events: DayEvents) = rereadNotesOf(events)
        .flatMap { (note, at) ->
            context.conceptsByNote[note].orEmpty().map { StruggleSignal(it.id, StruggleTrigger.REREAD, at) }
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
