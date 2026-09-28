package com.paulchibamba.margin.feature.celebration

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

fun Modifier.celebrationGlow(color: Color, radius: Dp): Modifier = drawBehind {
    val radiusPx = radius.toPx()
    drawCircle(Brush.radialGradient(listOf(color, Color.Transparent), center, radiusPx), radiusPx, center)
}
