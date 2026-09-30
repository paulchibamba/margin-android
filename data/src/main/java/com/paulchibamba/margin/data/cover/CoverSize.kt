package com.paulchibamba.margin.data.cover

import android.util.Size
import kotlin.math.roundToInt

object CoverSize {
    const val LONG_EDGE_PX = 600

    fun fit(width: Int, height: Int, longEdge: Int = LONG_EDGE_PX): Size {
        val scale = longEdge.toFloat() / maxOf(width, height)
        if (scale >= 1f) return Size(width, height)
        return Size((width * scale).roundToInt().coerceAtLeast(1), (height * scale).roundToInt().coerceAtLeast(1))
    }
}
