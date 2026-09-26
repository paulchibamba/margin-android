package com.paulchibamba.margin.designsystem

import androidx.compose.ui.graphics.Color

object Skins {
    val Ink = darkSkin("Ink", MarginColors.Ink, mutedAlpha = 0.6f)
    val Cobalt = darkSkin("Cobalt", MarginColors.Cobalt, mutedAlpha = 0.75f)
    val Paper = lightSkin("Paper", MarginColors.Paper, mutedContent = MarginColors.PaperTextMuted)
    val Ember = lightSkin("Ember", MarginColors.Ember, mutedContent = MarginColors.InkText.copy(alpha = 0.8f))
    val Forest = darkSkin("Forest", MarginColors.Forest, mutedAlpha = 0.75f)

    val all: List<Skin> = listOf(Ink, Cobalt, Paper, Ember, Forest)

    private fun darkSkin(name: String, background: Color, mutedAlpha: Float) = Skin(
        name = name,
        background = background,
        content = MarginColors.White,
        mutedContent = MarginColors.White.copy(alpha = mutedAlpha),
        surface = MarginColors.White.copy(alpha = 0.08f),
        correct = MarginColors.Lime,
        wrong = MarginColors.Wrong,
        isLight = false,
    )

    private fun lightSkin(name: String, background: Color, mutedContent: Color) = Skin(
        name = name,
        background = background,
        content = MarginColors.InkText,
        mutedContent = mutedContent,
        surface = MarginColors.InkText.copy(alpha = 0.07f),
        correct = MarginColors.CorrectOnLight,
        wrong = MarginColors.WrongOnLight,
        isLight = true,
    )
}
