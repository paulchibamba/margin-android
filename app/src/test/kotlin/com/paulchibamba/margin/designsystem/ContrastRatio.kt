package com.paulchibamba.margin.designsystem

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import kotlin.math.pow

object ContrastRatio {
    fun of(foreground: Color, background: Color): Double {
        val lighter = relativeLuminance(foreground.compositeOver(background))
        val darker = relativeLuminance(background)
        val (high, low) = if (lighter > darker) lighter to darker else darker to lighter
        return (high + 0.05) / (low + 0.05)
    }

    private fun relativeLuminance(color: Color): Double =
        0.2126 * linear(color.red) + 0.7152 * linear(color.green) + 0.0722 * linear(color.blue)

    private fun linear(channel: Float): Double {
        val value = channel.toDouble()
        return if (value <= 0.03928) value / 12.92 else ((value + 0.055) / 1.055).pow(2.4)
    }
}
