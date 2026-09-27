package com.paulchibamba.margin.designsystem.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
internal fun TextStyle.withChromeShadow(colors: FeedChromeColors, blur: Dp, alpha: Float): TextStyle {
    if (colors.shadow == Color.Transparent) return this
    val density = LocalDensity.current
    val shadow = with(density) {
        Shadow(colors.shadow.copy(alpha = alpha), Offset(0f, 1.dp.toPx()), blur.toPx())
    }
    return copy(shadow = shadow)
}
