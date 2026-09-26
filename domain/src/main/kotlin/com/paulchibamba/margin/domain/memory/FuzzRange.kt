package com.paulchibamba.margin.domain.memory

import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

internal data class FuzzRange(val shortest: Int, val longest: Int) {

    fun pick(fuzzFactor: Double): Int = floor(fuzzFactor * (longest - shortest + 1) + shortest).toInt()

    companion object {
        private data class Band(val start: Double, val end: Double, val factor: Double)

        private val BANDS = listOf(
            Band(start = 2.5, end = 7.0, factor = 0.15),
            Band(start = 7.0, end = 20.0, factor = 0.1),
            Band(start = 20.0, end = Double.POSITIVE_INFINITY, factor = 0.05),
        )

        fun of(interval: Int, elapsedDays: Int, maximumInterval: Int): FuzzRange {
            val delta = spreadAround(interval)
            val capped = min(interval, maximumInterval)
            val longest = min((capped + delta).roundToInt(), maximumInterval)
            val shortest = shortestFor(capped, delta, elapsedDays)
            return FuzzRange(shortest = min(shortest, longest), longest = longest)
        }

        private fun spreadAround(interval: Int): Double =
            1 + BANDS.sumOf { band -> band.factor * max(min(interval.toDouble(), band.end) - band.start, 0.0) }

        private fun shortestFor(interval: Int, delta: Double, elapsedDays: Int): Int {
            val shortest = max(2, (interval - delta).roundToInt())
            return if (interval > elapsedDays) max(shortest, elapsedDays + 1) else shortest
        }
    }
}
