package com.paulchibamba.margin.domain.memory

import java.time.Instant
import kotlin.time.Duration
import kotlin.time.toKotlinDuration
import java.time.Duration as JavaDuration

class FsrsScheduler(
    private val parameters: FsrsParameters = FsrsParameters.Default,
    private val fuzz: FuzzStrategy = CardSeededFuzz,
) {
    private val model = MemoryModel(parameters)
    private val learningSteps = LearningSteps(parameters.learningSteps, parameters.relearningSteps)

    fun createEmptyCard(now: Instant): MemoryCard = MemoryCard.new(now)

    fun next(card: MemoryCard, now: Instant, rating: Rating): MemoryCard =
        sessionFor(card, now).outcome(rating)

    fun previewIntervals(card: MemoryCard, now: Instant): Map<Rating, Duration> {
        val session = sessionFor(card, now)
        return Rating.entries.associateWith { rating -> timeUntil(session.outcome(rating).due, now) }
    }

    fun retrievability(card: MemoryCard, now: Instant): Double {
        val lastReview = card.lastReview
        if (card.state == CardState.NEW || lastReview == null) return 0.0
        val elapsedDays = DayCount.wholeDaysBetween(lastReview, now).coerceAtLeast(0)
        return model.forgettingCurve(elapsedDays, card.stability.roundedToEightDecimals())
    }

    private fun sessionFor(card: MemoryCard, now: Instant) = ReviewSession(
        card = card,
        reviewTime = now,
        model = model,
        learningSteps = learningSteps,
        fuzzFactor = fuzz.factorFor(card, now),
    )

    private fun timeUntil(due: Instant, now: Instant): Duration = JavaDuration.between(now, due).toKotlinDuration()
}
