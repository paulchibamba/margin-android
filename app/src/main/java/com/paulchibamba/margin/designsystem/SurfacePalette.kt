package com.paulchibamba.margin.designsystem

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val LocalSurfacePalette = staticCompositionLocalOf { SurfacePalette.Light }

@Immutable
data class SurfacePalette(
    val skin: Skin,
    val background: Color,
    val card: Color,
    val emphasisCard: Color,
    val divider: Color,
    val text: Color,
    val mutedText: Color,
    val faintText: Color,
    val aheadText: Color,
    val accent: Color,
    val onAccent: Color,
    val checkedSwitchThumb: Color,
    val correct: Color,
    val warningSurface: Color,
    val warningText: Color,
    val warningIcon: Color,
    val coverBackground: Color,
    val coverStripe: Color,
    val codeBackground: Color,
) {
    val isDark: Boolean get() = !skin.isLight

    companion object {
        val Light = SurfacePalette(
            skin = Skins.Paper,
            background = MarginColors.Paper,
            card = MarginColors.PaperCard,
            emphasisCard = MarginColors.InkText,
            divider = MarginColors.InkText.copy(alpha = 0.08f),
            text = MarginColors.InkText,
            mutedText = MarginColors.PaperTextMuted,
            faintText = MarginColors.PaperTextFaint,
            aheadText = MarginColors.PaperTextAhead,
            accent = MarginColors.InkText,
            onAccent = MarginColors.White,
            checkedSwitchThumb = MarginColors.Lime,
            correct = MarginColors.CorrectOnLight,
            warningSurface = MarginColors.WarningSurface,
            warningText = MarginColors.WarningText,
            warningIcon = MarginColors.SaveOnLight,
            coverBackground = MarginColors.PaperDivider,
            coverStripe = MarginColors.PaperStripe,
            codeBackground = MarginColors.Ink,
        )

        val Dark = SurfacePalette(
            skin = Skins.Ink,
            background = MarginColors.Ink,
            card = MarginColors.InkSheet,
            emphasisCard = MarginColors.InkCard,
            divider = MarginColors.White.copy(alpha = 0.08f),
            text = MarginColors.White,
            mutedText = MarginColors.White.copy(alpha = 0.75f),
            faintText = MarginColors.White.copy(alpha = 0.6f),
            aheadText = MarginColors.White.copy(alpha = 0.55f),
            accent = MarginColors.Lime,
            onAccent = MarginColors.InkText,
            checkedSwitchThumb = MarginColors.InkText,
            correct = MarginColors.Lime,
            warningSurface = MarginColors.WarningSurfaceOnInk,
            warningText = MarginColors.WarningTextOnInk,
            warningIcon = MarginColors.Save,
            coverBackground = MarginColors.InkStripe,
            coverStripe = MarginColors.InkSnackbar,
            codeBackground = MarginColors.InkCard,
        )

        val all: List<SurfacePalette> = listOf(Light, Dark)

        fun of(isDark: Boolean): SurfacePalette = if (isDark) Dark else Light
    }
}
