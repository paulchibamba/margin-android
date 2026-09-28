package com.paulchibamba.margin.domain.memory

import kotlin.math.roundToInt

object DesiredRetention {
    const val DEFAULT = 0.90
    const val STEEP_WORKLOAD_ABOVE = 0.90
    val RANGE = 0.80..0.95

    fun normalise(retention: Double): Double = ((retention * 100).roundToInt() / 100.0).coerceIn(RANGE)
}
