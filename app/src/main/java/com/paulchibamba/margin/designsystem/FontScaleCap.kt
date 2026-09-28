package com.paulchibamba.margin.designsystem

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType

const val CHROME_FONT_SCALE_CAP = 1.3f

@Composable
fun TextStyle.withFontScaleCap(cap: Float = CHROME_FONT_SCALE_CAP): TextStyle {
    val shrink = shrinkFor(LocalDensity.current.fontScale, cap)
    if (shrink == 1f) return this
    return copy(fontSize = fontSize.shrunkBy(shrink), lineHeight = lineHeight.shrunkBy(shrink))
}

fun shrinkFor(fontScale: Float, cap: Float): Float = if (fontScale <= cap) 1f else cap / fontScale

private fun TextUnit.shrunkBy(factor: Float): TextUnit = if (type == TextUnitType.Sp) this * factor else this
