package com.paulchibamba.margin.domain.progress

import com.paulchibamba.margin.domain.model.Concept
import com.paulchibamba.margin.domain.rollup.DayEvents
import java.time.Duration
import java.time.Instant
import java.time.ZoneId

class ProgressFactsCalculator {

    fun factsOf(sources: ProgressSources, now: Instant, zone: ZoneId): ProgressFacts {
        val context = ProgressContext(sources, now, zone)
        val events = DayEvents(sources.events.filterNot { it.at.isBefore(windowStart(now)) }, zone)
        val attention = AttentionFactsCalculator.of(context, events)
        val introductions = IntroductionDates.of(context)
        val struggles = StruggleFinder.of(context, events, attention, since = windowStart(now))
        val remembered = context.countedConcepts.filter(context::isRemembered)
        return ProgressFacts(
            today = context.today,
            zone = zone,
            chapters = ChapterProgressCalculator.of(context, introductions),
            introduced = context.countedConcepts.filter(context::isIntroduced),
            introductions = introductions,
            comebacks = ComebackFinder.of(context),
            remembered = remembered,
            newlyRemembered = remembered.filter { concept -> isNewlyRemembered(concept, context) },
            upcomingNotes = UpcomingNotesFinder.of(context),
            unquotedReadNotes = ReadNotesFinder.unquoted(context),
            attention = attention,
            struggles = struggles,
            anglesShown = struggles.associate { it.concept.id to anglesOf(it.concept, context) },
            relations = ConceptRelations(sources.concepts),
        )
    }

    private fun isNewlyRemembered(concept: Concept, context: ProgressContext): Boolean {
        val lastReview = context.progressOf(concept).card?.lastReview ?: return false
        val since = context.sources.history.lastBakeAt ?: context.now.minus(UNBAKED_LOOKBACK)
        val isCelebrated = context.sources.history.of(RewardKind.NowYouCan, concept.id).isNotEmpty()
        return lastReview.isAfter(since) && !isCelebrated
    }

    private fun anglesOf(concept: Concept, context: ProgressContext): List<Angle> =
        context.postsByConcept[concept.id].orEmpty()
            .filter { post -> context.sources.feedState.hasSeen(post.id) }
            .map { post -> Angle.of(post.format, post.content.title) }

    companion object {
        val ATTENTION_WINDOW: Duration = Duration.ofDays(14)
        private val UNBAKED_LOOKBACK: Duration = Duration.ofDays(7)

        fun windowStart(now: Instant): Instant = now.minus(ATTENTION_WINDOW)
    }
}
