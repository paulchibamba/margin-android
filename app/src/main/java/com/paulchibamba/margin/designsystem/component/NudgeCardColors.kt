package com.paulchibamba.margin.designsystem.component

import androidx.compose.ui.graphics.Color
import com.paulchibamba.margin.designsystem.MarginColors
import com.paulchibamba.margin.designsystem.PostCardStyle

class NudgeCardColors(
    val background: Color,
    val icon: Color,
    val title: Color,
    val detail: Color,
    val button: Color,
    val onButton: Color,
    val secondaryButton: Color,
) {
    companion object {
        val Contrasting = NudgeCardColors(
            background = MarginColors.PaperCard,
            icon = MarginColors.Cobalt,
            title = MarginColors.InkText,
            detail = MarginColors.PaperTextMuted,
            button = MarginColors.InkText,
            onButton = MarginColors.White,
            secondaryButton = MarginColors.InkText,
        )
        val Dark = NudgeCardColors(
            background = MarginColors.InkSnackbar,
            icon = MarginColors.Mint,
            title = MarginColors.White,
            detail = MarginColors.White.copy(alpha = 0.75f),
            button = MarginColors.Lime,
            onButton = MarginColors.InkText,
            secondaryButton = MarginColors.White,
        )

        fun of(style: PostCardStyle): NudgeCardColors = if (style == PostCardStyle.DARK) Dark else Contrasting
    }
}
