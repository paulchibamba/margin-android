package com.paulchibamba.margin.designsystem

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SurfacePaletteContrastTest {

    @Test
    fun `every text colour meets 4 point 5 to 1 on the background and on cards in both palettes`() {
        SurfacePalette.all.forEach { palette ->
            listOf(palette.background, palette.card).forEach { surface ->
                textColoursOf(palette).forEach { (role, color) ->
                    assertContrast("${palette.skin.name} $role", color, surface, BODY_TEXT)
                }
            }
        }
    }

    @Test
    fun `text on the accent and the correct mark read in both palettes`() {
        SurfacePalette.all.forEach { palette ->
            assertContrast("${palette.skin.name} on accent", palette.onAccent, palette.accent, BODY_TEXT)
            assertContrast("${palette.skin.name} read mark", palette.correct, palette.card, BODY_TEXT)
        }
    }

    @Test
    fun `the checked switch thumb stands out from its track in both palettes`() {
        SurfacePalette.all.forEach { palette ->
            assertContrast("${palette.skin.name} switch", palette.checkedSwitchThumb, palette.accent, NON_TEXT)
        }
    }

    @Test
    fun `the workload warning reads in both palettes`() {
        SurfacePalette.all.forEach { palette ->
            assertContrast("${palette.skin.name} warning", palette.warningText, palette.warningSurface, BODY_TEXT)
            assertContrast("${palette.skin.name} warning icon", palette.warningIcon, palette.warningSurface, NON_TEXT)
        }
    }

    @Test
    fun `white text reads on the emphasis card in both palettes`() {
        SurfacePalette.all.forEach { palette ->
            val faintWhite = MarginColors.White.copy(alpha = 0.6f)
            assertContrast("${palette.skin.name} continue card", faintWhite, palette.emphasisCard, BODY_TEXT)
        }
    }

    @Test
    fun `code blocks read on the note in both palettes`() {
        SurfacePalette.all.forEach { palette ->
            assertContrast("${palette.skin.name} code", MarginColors.CodeText, palette.codeBackground, BODY_TEXT)
        }
    }

    @Test
    fun `the dark palette gives light status-bar icons and the light palette dark ones`() {
        assertEquals(true, SurfacePalette.Light.skin.isLight)
        assertEquals(false, SurfacePalette.Dark.skin.isLight)
        assertEquals(SurfacePalette.Dark, SurfacePalette.of(isDark = true))
        assertEquals(SurfacePalette.Light, SurfacePalette.of(isDark = false))
    }

    private fun textColoursOf(palette: SurfacePalette) = listOf(
        "text" to palette.text,
        "muted text" to palette.mutedText,
        "faint text" to palette.faintText,
        "ahead text" to palette.aheadText,
    )

    private fun assertContrast(what: String, foreground: Color, background: Color, minimum: Double) {
        val ratio = ContrastRatio.of(foreground, background.compositeOver(Color.Black))
        assertTrue(ratio >= minimum, "$what contrast is ${"%.2f".format(ratio)}, below $minimum")
    }

    private companion object {
        const val BODY_TEXT = 4.5
        const val NON_TEXT = 3.0
    }
}
