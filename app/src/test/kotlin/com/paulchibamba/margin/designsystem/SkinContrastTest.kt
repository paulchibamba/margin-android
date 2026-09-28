package com.paulchibamba.margin.designsystem

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import org.junit.Test
import kotlin.test.assertTrue

class SkinContrastTest {

    @Test
    fun `content text meets 4 point 5 to 1 on every skin`() {
        Skins.all.forEach { assertContrast("${it.name} content", it.content, it.background, BODY_TEXT) }
    }

    @Test
    fun `muted text meets 4 point 5 to 1 on every skin`() {
        Skins.all.forEach { assertContrast("${it.name} muted", it.mutedContent, it.background, BODY_TEXT) }
    }

    @Test
    fun `headlines in content colour meet 3 to 1 on every skin`() {
        Skins.all.forEach { assertContrast("${it.name} headline", it.content, it.background, LARGE_TEXT) }
    }

    @Test
    fun `right, wrong and save colours read as text on every skin and its surface`() {
        Skins.all.forEach { skin ->
            signalsOf(skin).forEach { (role, color) ->
                assertContrast("${skin.name} $role", color, skin.background, BODY_TEXT)
                val surface = skin.surface.compositeOver(skin.background)
                assertContrast("${skin.name} $role on surface", color, surface, BODY_TEXT)
            }
        }
    }

    @Test
    fun `answer text reads on its own tinted background on every skin`() {
        Skins.all.forEach { skin ->
            listOf("right" to skin.correct, "wrong" to skin.wrong).forEach { (role, color) ->
                val tint = color.copy(alpha = ANSWER_TINT).compositeOver(skin.background)
                assertContrast("${skin.name} $role on tint", color, tint, BODY_TEXT)
            }
        }
    }

    @Test
    fun `paper greys read on paper and on paper cards`() {
        val greys = listOf(
            MarginColors.PaperTextStrong,
            MarginColors.PaperTextMuted,
            MarginColors.PaperTextFaint,
            MarginColors.PaperTextAhead,
        )
        listOf(MarginColors.Paper, MarginColors.PaperCard).forEach { background ->
            greys.forEach { grey -> assertContrast("grey $grey on $background", grey, background, BODY_TEXT) }
        }
    }

    @Test
    fun `the dimmest white text reads on every ink surface`() {
        val dimmestWhite = MarginColors.White.copy(alpha = DIMMEST_WHITE)
        listOf(MarginColors.Ink, MarginColors.InkSheet, MarginColors.InkRaised, MarginColors.InkCard).forEach {
            assertContrast("white on $it", dimmestWhite, it, BODY_TEXT)
        }
    }

    @Test
    fun `buttons, warnings and swipe stamps are legible`() {
        assertContrast("ink on lime", MarginColors.InkText, MarginColors.Lime, BODY_TEXT)
        assertContrast("ink on white", MarginColors.InkText, MarginColors.White, BODY_TEXT)
        assertContrast("warning", MarginColors.WarningText, MarginColors.WarningSurface, BODY_TEXT)
        assertContrast("false stamp on paper", MarginColors.WrongStamp, MarginColors.Paper, LARGE_TEXT)
        assertContrast("true stamp on paper", MarginColors.CorrectOnLight, MarginColors.Paper, LARGE_TEXT)
        assertContrast("false stamp on ink", MarginColors.Wrong, MarginColors.InkCard, LARGE_TEXT)
        assertContrast("true stamp on ink", MarginColors.Lime, MarginColors.InkCard, LARGE_TEXT)
    }

    @Test
    fun `white on black has the maximum contrast of 21 to 1`() {
        val ratio = ContrastRatio.of(MarginColors.White, Color.Black)
        assertTrue(ratio in 20.99..21.01, "was $ratio")
    }

    private fun signalsOf(skin: Skin) = listOf("right" to skin.correct, "wrong" to skin.wrong, "save" to skin.save)

    private fun assertContrast(what: String, foreground: Color, background: Color, minimum: Double) {
        val ratio = ContrastRatio.of(foreground, background)
        assertTrue(ratio >= minimum, "$what contrast is ${"%.2f".format(ratio)}, below $minimum")
    }

    private companion object {
        const val BODY_TEXT = 4.5
        const val LARGE_TEXT = 3.0
        const val ANSWER_TINT = 0.14f
        const val DIMMEST_WHITE = 0.55f
    }
}
