package com.paulchibamba.margin.domain.memory

import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.time.Duration

internal class LearningSteps(
    private val learningSteps: List<Duration>,
    private val relearningSteps: List<Duration>,
) {

    fun outcome(state: CardState, currentStep: Int, rating: Rating): StepOutcome? {
        val steps = stepsFor(state)
        if (steps.isEmpty() || currentStep >= steps.size) return null
        return if (state == CardState.REVIEW) {
            lapseOutcome(steps, currentStep, rating)
        } else {
            learningOutcome(steps.map(Duration::inWholeMinutes), currentStep, rating)
        }
    }

    private fun stepsFor(state: CardState): List<Duration> =
        if (state == CardState.REVIEW || state == CardState.RELEARNING) relearningSteps else learningSteps

    private fun lapseOutcome(steps: List<Duration>, currentStep: Int, rating: Rating): StepOutcome? {
        if (rating != Rating.AGAIN) return null
        val minutes = steps[max(0, currentStep)].inWholeMinutes.toInt()
        return StepOutcome(scheduledMinutes = minutes, nextStep = 0)
    }

    private fun learningOutcome(minutes: List<Long>, currentStep: Int, rating: Rating): StepOutcome? =
        when (rating) {
            Rating.AGAIN -> StepOutcome(scheduledMinutes = minutes.first().toInt(), nextStep = 0)
            Rating.HARD -> StepOutcome(scheduledMinutes = hardMinutes(minutes), nextStep = currentStep)
            Rating.GOOD -> goodOutcome(minutes, currentStep)
            Rating.EASY -> null
        }

    private fun hardMinutes(minutes: List<Long>): Int =
        if (minutes.size == 1) {
            (minutes.first() * 1.5).roundToInt()
        } else {
            ((minutes[0] + minutes[1]) / 2.0).roundToInt()
        }

    private fun goodOutcome(minutes: List<Long>, currentStep: Int): StepOutcome? {
        val nextMinutes = minutes.getOrNull(currentStep + 1)?.takeIf { it > 0 } ?: return null
        return StepOutcome(scheduledMinutes = nextMinutes.toInt(), nextStep = currentStep + 1)
    }
}
