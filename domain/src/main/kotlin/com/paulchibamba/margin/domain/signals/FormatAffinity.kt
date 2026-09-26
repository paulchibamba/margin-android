package com.paulchibamba.margin.domain.signals

import com.paulchibamba.margin.domain.model.Format

data class FormatAffinity(
    val values: Map<Format, Double> = emptyMap(),
    private val learningRate: Double = DEFAULT_LEARNING_RATE,
) {
    fun valueOf(format: Format): Double = values[format] ?: NEUTRAL

    fun afterEngagement(format: Format, engagement: Double): FormatAffinity {
        val current = valueOf(format)
        return withValue(format, current + learningRate * (engagement - current))
    }

    fun afterLess(format: Format): FormatAffinity = withValue(format, valueOf(format) * LESS_FACTOR)

    private fun withValue(format: Format, value: Double) = copy(values = values + (format to value))

    companion object {
        const val NEUTRAL = 0.5
        const val DEFAULT_LEARNING_RATE = 0.25
        private const val LESS_FACTOR = 0.5
    }
}
