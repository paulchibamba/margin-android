package com.paulchibamba.margin.designsystem.component

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import com.paulchibamba.margin.designsystem.ContrastRatio
import com.paulchibamba.margin.designsystem.PostCardStyle
import org.junit.Test
import kotlin.test.assertSame
import kotlin.test.assertTrue

class NudgeCardColorsTest {

    @Test
    fun `dark posts get the dark nudge card and otherwise it stays the paper card`() {
        assertSame(NudgeCardColors.Dark, NudgeCardColors.of(PostCardStyle.DARK))
        assertSame(NudgeCardColors.Contrasting, NudgeCardColors.of(PostCardStyle.CONTRASTING))
    }

    @Test
    fun `nudge text and buttons read on both nudge cards`() {
        listOf(NudgeCardColors.Contrasting, NudgeCardColors.Dark).forEach { colors ->
            assertContrast(colors.title, colors.background, BODY_TEXT)
            assertContrast(colors.detail, colors.background, BODY_TEXT)
            assertContrast(colors.secondaryButton, colors.background, BODY_TEXT)
            assertContrast(colors.onButton, colors.button, BODY_TEXT)
            assertContrast(colors.icon, colors.background, NON_TEXT)
        }
    }

    private fun assertContrast(foreground: Color, background: Color, minimum: Double) {
        val ratio = ContrastRatio.of(foreground, background.compositeOver(Color.Black))
        assertTrue(ratio >= minimum, "contrast is ${"%.2f".format(ratio)}, below $minimum")
    }

    private companion object {
        const val BODY_TEXT = 4.5
        const val NON_TEXT = 3.0
    }
}
