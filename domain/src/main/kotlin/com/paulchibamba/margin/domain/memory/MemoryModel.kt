package com.paulchibamba.margin.domain.memory

import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt

internal class MemoryModel(private val parameters: FsrsParameters) {

    private val w = parameters.weights
    private val decay = -w[20]
    private val factor = (exp(ln(0.9) / decay) - 1).roundedToEightDecimals()
    private val intervalModifier = ((parameters.requestRetention.pow(1 / decay) - 1) / factor).roundedToEightDecimals()

    fun forgettingCurve(elapsedDays: Int, stability: Double): Double =
        (1 + factor * elapsedDays / stability).pow(decay).roundedToEightDecimals()

    fun nextMemoryState(
        current: MemoryState,
        elapsedDays: Int,
        rating: Rating,
        retrievability: Double? = null,
    ): MemoryState {
        if (current.isUnset) return initialMemoryState(rating)
        val recallProbability = retrievability ?: forgettingCurve(elapsedDays, current.stability)
        return MemoryState(
            stability = nextStability(current, elapsedDays, rating, recallProbability),
            difficulty = nextDifficulty(current.difficulty, rating),
        )
    }

    fun nextInterval(stability: Double, elapsedDays: Int, fuzzFactor: Double?): Int {
        val interval = (stability * intervalModifier).roundToInt()
            .coerceAtLeast(1)
            .coerceAtMost(parameters.maximumInterval)
        return if (fuzzFactor == null) interval else fuzzed(interval, elapsedDays, fuzzFactor)
    }

    private fun initialMemoryState(rating: Rating) = MemoryState(
        stability = initialStability(rating),
        difficulty = initialDifficulty(rating).coerceIn(MIN_DIFFICULTY, MAX_DIFFICULTY),
    )

    private fun initialStability(rating: Rating): Double = max(w[rating.value - 1], 0.1)

    private fun initialDifficulty(rating: Rating): Double =
        (w[4] - exp((rating.value - 1) * w[5]) + 1).roundedToEightDecimals()

    private fun nextStability(current: MemoryState, elapsedDays: Int, rating: Rating, retrievability: Double) =
        when {
            elapsedDays == 0 -> nextShortTermStability(current.stability, rating)
            rating == Rating.AGAIN -> nextStabilityAfterLapse(current, retrievability)
            else -> nextRecallStability(current, retrievability, rating)
        }

    private fun nextDifficulty(difficulty: Double, rating: Rating): Double {
        val delta = -w[6] * (rating.value - 3)
        val damped = difficulty + linearDamping(delta, difficulty)
        return meanReversion(initialDifficulty(Rating.EASY), damped).coerceIn(MIN_DIFFICULTY, MAX_DIFFICULTY)
    }

    private fun linearDamping(delta: Double, difficulty: Double): Double =
        (delta * (10 - difficulty) / 9).roundedToEightDecimals()

    private fun meanReversion(initial: Double, current: Double): Double =
        (w[7] * initial + (1 - w[7]) * current).roundedToEightDecimals()

    private fun nextRecallStability(current: MemoryState, retrievability: Double, rating: Rating): Double {
        val (stability, difficulty) = current
        val hardPenalty = if (rating == Rating.HARD) w[15] else 1.0
        val easyBonus = if (rating == Rating.EASY) w[16] else 1.0
        val growth = exp(w[8]) * (11 - difficulty) * stability.pow(-w[9]) *
            (exp((1 - retrievability) * w[10]) - 1) * hardPenalty * easyBonus
        return (stability * (1 + growth)).withinStabilityBounds()
    }

    private fun nextStabilityAfterLapse(current: MemoryState, retrievability: Double): Double {
        val afterForgetting = nextForgetStability(current, retrievability)
        val floor = (current.stability / exp(w[17] * w[18])).roundedToEightDecimals()
        return min(max(floor, MIN_STABILITY), afterForgetting)
    }

    private fun nextForgetStability(current: MemoryState, retrievability: Double): Double {
        val (stability, difficulty) = current
        val forgotten = w[11] * difficulty.pow(-w[12]) * ((stability + 1).pow(w[13]) - 1) *
            exp((1 - retrievability) * w[14])
        return forgotten.withinStabilityBounds()
    }

    private fun nextShortTermStability(stability: Double, rating: Rating): Double {
        val increase = stability.pow(-w[19]) * exp(w[17] * (rating.value - 3 + w[18]))
        val maskedIncrease = if (rating >= Rating.HARD) max(increase, 1.0) else increase
        return (stability * maskedIncrease).withinStabilityBounds()
    }

    private fun fuzzed(interval: Int, elapsedDays: Int, fuzzFactor: Double): Int {
        if (interval < MIN_FUZZABLE_INTERVAL) return interval
        val range = FuzzRange.of(interval, elapsedDays, parameters.maximumInterval)
        return range.pick(fuzzFactor)
    }

    private fun Double.withinStabilityBounds(): Double =
        coerceIn(MIN_STABILITY, MAX_STABILITY).roundedToEightDecimals()

    private companion object {
        const val MIN_STABILITY = 0.001
        const val MAX_STABILITY = 36500.0
        const val MIN_DIFFICULTY = 1.0
        const val MAX_DIFFICULTY = 10.0
        const val MIN_FUZZABLE_INTERVAL = 2.5
    }
}
