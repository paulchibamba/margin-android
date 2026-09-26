package com.paulchibamba.margin.domain.memory

import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

data class FsrsParameters(
    val requestRetention: Double,
    val weights: List<Double>,
    val maximumInterval: Int,
    val learningSteps: List<Duration>,
    val relearningSteps: List<Duration>,
) {
    init {
        require(requestRetention > 0.0 && requestRetention <= 1.0) { "Retention must be in (0, 1]" }
        require(weights.size == WEIGHT_COUNT) { "FSRS-6 needs $WEIGHT_COUNT weights, got ${weights.size}" }
    }

    companion object {
        private const val WEIGHT_COUNT = 21

        val Default = FsrsParameters(
            requestRetention = 0.9,
            weights = listOf(
                0.212, 1.2931, 2.3065, 8.2956, 6.4133, 0.8334, 3.0194, 0.001, 1.8722, 0.1666, 0.796,
                1.4835, 0.0614, 0.2629, 1.6483, 0.6014, 1.8729, 0.5425, 0.0912, 0.0658, 0.1542,
            ),
            maximumInterval = 36500,
            learningSteps = listOf(1.minutes, 10.minutes),
            relearningSteps = listOf(10.minutes),
        )
    }
}
