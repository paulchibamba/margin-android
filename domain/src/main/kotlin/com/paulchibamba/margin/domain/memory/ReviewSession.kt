package com.paulchibamba.margin.domain.memory

import java.time.Instant
import kotlin.math.max
import kotlin.math.min
import java.time.Duration as JavaDuration

internal class ReviewSession(
    private val card: MemoryCard,
    private val reviewTime: Instant,
    private val model: MemoryModel,
    private val learningSteps: LearningSteps,
    private val fuzzFactor: Double?,
) {
    private val elapsedDays = daysSinceLastReview()
    private val reviewed = card.copy(lastReview = reviewTime, reps = card.reps + 1)

    fun outcome(rating: Rating): MemoryCard = when (card.state) {
        CardState.NEW -> afterLearningReview(rating, stillLearningState = CardState.LEARNING)
        CardState.LEARNING, CardState.RELEARNING -> afterLearningReview(rating, stillLearningState = card.state)
        CardState.REVIEW -> afterScheduledReview(rating)
    }

    private fun daysSinceLastReview(): Int {
        val lastReview = card.lastReview
        return if (card.state == CardState.NEW || lastReview == null) 0
        else DayCount.calendarDaysBetween(lastReview, reviewTime)
    }

    private fun afterLearningReview(rating: Rating, stillLearningState: CardState): MemoryCard {
        val remembered = reviewed.withMemory(model.nextMemoryState(reviewed.memoryState, elapsedDays, rating))
        return applyLearningSteps(remembered, rating, stillLearningState)
    }

    private fun afterScheduledReview(rating: Rating): MemoryCard {
        val retrievability = model.forgettingCurve(elapsedDays, reviewed.stability)
        return if (rating == Rating.AGAIN) {
            afterLapse(retrievability)
        } else {
            afterRecall(rating, retrievability)
        }
    }

    private fun afterLapse(retrievability: Double): MemoryCard {
        val forgotten = remember(Rating.AGAIN, retrievability)
        val relearning = applyLearningSteps(forgotten, Rating.AGAIN, CardState.RELEARNING)
        return relearning.copy(lapses = relearning.lapses + 1)
    }

    private fun afterRecall(rating: Rating, retrievability: Double): MemoryCard {
        val outcomes = RecallOutcomes(
            hard = remember(Rating.HARD, retrievability),
            good = remember(Rating.GOOD, retrievability),
            easy = remember(Rating.EASY, retrievability),
        )
        val chosen = outcomes.cardFor(rating)
        return scheduledInReview(chosen, outcomes.intervalFor(rating))
    }

    private fun remember(rating: Rating, retrievability: Double): MemoryCard =
        reviewed.withMemory(model.nextMemoryState(reviewed.memoryState, elapsedDays, rating, retrievability))

    private fun applyLearningSteps(next: MemoryCard, rating: Rating, stillLearningState: CardState): MemoryCard {
        val step = learningSteps.outcome(reviewed.state, reviewed.learningSteps, rating)
        val minutes = max(0, step?.scheduledMinutes ?: 0)
        val nextStep = max(0, step?.nextStep ?: 0)
        return when {
            minutes in 1 until MINUTES_PER_DAY -> stillLearning(next, stillLearningState, minutes, nextStep)
            minutes >= MINUTES_PER_DAY -> graduatedAfterLongStep(next, minutes, nextStep)
            else -> scheduledInReview(next, model.nextInterval(next.stability, elapsedDays, fuzzFactor))
        }
    }

    private fun stillLearning(next: MemoryCard, state: CardState, minutes: Int, nextStep: Int) = next.copy(
        state = state,
        learningSteps = nextStep,
        scheduledDays = 0,
        due = reviewTime.plus(JavaDuration.ofMinutes(minutes.toLong())),
    )

    private fun graduatedAfterLongStep(next: MemoryCard, minutes: Int, nextStep: Int) = next.copy(
        state = CardState.REVIEW,
        learningSteps = nextStep,
        scheduledDays = minutes / MINUTES_PER_DAY,
        due = reviewTime.plus(JavaDuration.ofMinutes(minutes.toLong())),
    )

    private fun scheduledInReview(next: MemoryCard, intervalDays: Int) = next.copy(
        state = CardState.REVIEW,
        learningSteps = 0,
        scheduledDays = intervalDays,
        due = reviewTime.plus(JavaDuration.ofDays(intervalDays.toLong())),
    )

    private inner class RecallOutcomes(val hard: MemoryCard, val good: MemoryCard, val easy: MemoryCard) {
        private val hardInterval: Int
        private val goodInterval: Int
        private val easyInterval: Int

        init {
            val rawHard = model.nextInterval(hard.stability, elapsedDays, fuzzFactor)
            val rawGood = model.nextInterval(good.stability, elapsedDays, fuzzFactor)
            hardInterval = min(rawHard, rawGood)
            goodInterval = max(rawGood, hardInterval + 1)
            easyInterval = max(model.nextInterval(easy.stability, elapsedDays, fuzzFactor), goodInterval + 1)
        }

        fun cardFor(rating: Rating): MemoryCard = when (rating) {
            Rating.HARD -> hard
            Rating.GOOD -> good
            else -> easy
        }

        fun intervalFor(rating: Rating): Int = when (rating) {
            Rating.HARD -> hardInterval
            Rating.GOOD -> goodInterval
            else -> easyInterval
        }
    }

    private companion object {
        const val MINUTES_PER_DAY = 1440
    }
}
