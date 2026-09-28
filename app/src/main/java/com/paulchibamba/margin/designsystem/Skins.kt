package com.paulchibamba.margin.designsystem

import androidx.compose.ui.graphics.Color

object Skins {
    private class Signals(val correct: Color, val wrong: Color, val save: Color)

    private val LightSignals = Signals(MarginColors.CorrectOnLight, MarginColors.WrongOnLight, MarginColors.SaveOnLight)

    val Ink = darkSkin("Ink", MarginColors.Ink, mutedAlpha = 0.6f)
    val Cobalt = darkSkin("Cobalt", MarginColors.Cobalt, mutedAlpha = 0.75f, wrong = MarginColors.WrongOnCobalt)
    val Paper = lightSkin("Paper", MarginColors.Paper, mutedContent = MarginColors.PaperTextMuted)
    val Ember = lightSkin(
        "Ember",
        MarginColors.Ember,
        mutedContent = MarginColors.InkText.copy(alpha = 0.8f),
        signals = Signals(MarginColors.CorrectOnEmber, MarginColors.WrongOnEmber, MarginColors.SaveOnEmber),
    )
    val Forest = darkSkin("Forest", MarginColors.Forest, mutedAlpha = 0.75f, wrong = MarginColors.WrongOnForest)

    val all: List<Skin> = listOf(Ink, Cobalt, Paper, Ember, Forest)

    private fun darkSkin(name: String, background: Color, mutedAlpha: Float, wrong: Color = MarginColors.Wrong) = Skin(
        name = name,
        background = background,
        content = MarginColors.White,
        mutedContent = MarginColors.White.copy(alpha = mutedAlpha),
        surface = MarginColors.White.copy(alpha = 0.08f),
        correct = MarginColors.Lime,
        wrong = wrong,
        save = MarginColors.Save,
        isLight = false,
    )

    private fun lightSkin(name: String, background: Color, mutedContent: Color, signals: Signals = LightSignals) = Skin(
        name = name,
        background = background,
        content = MarginColors.InkText,
        mutedContent = mutedContent,
        surface = MarginColors.InkText.copy(alpha = 0.07f),
        correct = signals.correct,
        wrong = signals.wrong,
        save = signals.save,
        isLight = true,
    )
}
