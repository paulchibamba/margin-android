package com.paulchibamba.margin.domain.signals

import com.paulchibamba.margin.domain.model.AffinityKey
import com.paulchibamba.margin.domain.model.Format

data class FormatAffinity(
    val values: Map<Format, Double> = emptyMap(),
    val rewardKindValues: Map<String, Double> = emptyMap(),
    private val learningRate: Double = DEFAULT_LEARNING_RATE,
) {
    fun valueOf(format: Format): Double = valueOf(AffinityKey.OfFormat(format))

    fun valueOf(key: AffinityKey): Double = when (key) {
        is AffinityKey.OfFormat -> values[key.format]
        is AffinityKey.OfRewardKind -> rewardKindValues[key.kind]
    } ?: NEUTRAL

    fun afterEngagement(format: Format, engagement: Double): FormatAffinity =
        afterEngagement(AffinityKey.OfFormat(format), engagement)

    fun afterEngagement(key: AffinityKey, engagement: Double): FormatAffinity {
        val current = valueOf(key)
        return withValue(key, current + learningRate * (engagement - current))
    }

    fun afterLess(format: Format): FormatAffinity = afterLess(AffinityKey.OfFormat(format))

    fun afterLess(key: AffinityKey): FormatAffinity = withValue(key, valueOf(key) * LESS_FACTOR)

    private fun withValue(key: AffinityKey, value: Double) = when (key) {
        is AffinityKey.OfFormat -> copy(values = values + (key.format to value))
        is AffinityKey.OfRewardKind -> copy(rewardKindValues = rewardKindValues + (key.kind to value))
    }

    companion object {
        const val NEUTRAL = 0.5
        const val DEFAULT_LEARNING_RATE = 0.25
        private const val LESS_FACTOR = 0.5
    }
}
