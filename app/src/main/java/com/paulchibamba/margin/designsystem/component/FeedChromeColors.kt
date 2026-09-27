package com.paulchibamba.margin.designsystem.component

import androidx.compose.ui.graphics.Color
import com.paulchibamba.margin.designsystem.MarginColors

internal class FeedChromeColors private constructor(
    val content: Color,
    val chip: Color,
    val pill: Color,
    val outline: Color,
    val segmentTrack: Color,
    val progressTrack: Color,
    val gotIt: Color,
    val lost: Color,
    val save: Color,
    val shadow: Color,
) {
    companion object {
        private val Light = FeedChromeColors(
            content = MarginColors.InkText,
            chip = MarginColors.InkText.copy(alpha = 0.07f),
            pill = MarginColors.InkText.copy(alpha = 0.09f),
            outline = MarginColors.InkText.copy(alpha = 0.35f),
            segmentTrack = MarginColors.InkText.copy(alpha = 0.18f),
            progressTrack = MarginColors.InkText.copy(alpha = 0.15f),
            gotIt = MarginColors.CorrectOnLight,
            lost = MarginColors.WrongOnLight,
            save = MarginColors.SaveOnLight,
            shadow = Color.Transparent,
        )
        private val Dark = FeedChromeColors(
            content = MarginColors.White,
            chip = MarginColors.White.copy(alpha = 0.12f),
            pill = MarginColors.White.copy(alpha = 0.16f),
            outline = MarginColors.White.copy(alpha = 0.55f),
            segmentTrack = MarginColors.White.copy(alpha = 0.3f),
            progressTrack = MarginColors.White.copy(alpha = 0.25f),
            gotIt = MarginColors.Lime,
            lost = MarginColors.Wrong,
            save = MarginColors.Save,
            shadow = Color.Black,
        )

        fun of(isLight: Boolean) = if (isLight) Light else Dark
    }
}
