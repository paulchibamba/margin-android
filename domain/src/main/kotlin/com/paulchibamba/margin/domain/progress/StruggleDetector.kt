package com.paulchibamba.margin.domain.progress

import com.paulchibamba.margin.domain.actions.ActionLogEntry
import com.paulchibamba.margin.domain.feed.ReviewLogEntry
import com.paulchibamba.margin.domain.model.Concept
import com.paulchibamba.margin.domain.rollup.DayEvents
import com.paulchibamba.margin.domain.tracking.LoggedEvent
import java.time.Instant
import java.time.ZoneId

class StruggleDetector {

    fun strugglesOf(
        concepts: List<Concept>,
        reviews: List<ReviewLogEntry>,
        actions: List<ActionLogEntry>,
        events: List<LoggedEvent>,
        zone: ZoneId,
        now: Instant,
    ): List<Struggle> {
        val since = ProgressFactsCalculator.windowStart(now)
        val recent = DayEvents(events.filterNot { event -> event.at.isBefore(since) }, zone)
        val attention = AttentionFacts(hotspots = emptyList(), AttentionFactsCalculator.glancedOnlyOf(recent))
        return StruggleFinder.of(LoggedStruggleInputs(concepts, reviews, actions), recent, attention, since)
    }
}
