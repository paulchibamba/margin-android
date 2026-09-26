package com.paulchibamba.margin.designsystem

import androidx.compose.ui.graphics.Color

enum class BrandTile(val background: Color, val rule: Color, val lines: Color, val outline: Color) {
    Dark(MarginColors.Ink, MarginColors.Lime, MarginColors.White, Color.Transparent),
    Lime(MarginColors.Lime, MarginColors.Ink, MarginColors.Ink, Color.Transparent),
    Light(MarginColors.Paper, MarginColors.DeepLime, MarginColors.Ink, Color.Black.copy(alpha = 0.08f)),
}
