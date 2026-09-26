package com.paulchibamba.margin.designsystem

import androidx.compose.ui.graphics.Color
import org.junit.Test
import kotlin.test.assertTrue

class SkinContrastTest {

    @Test
    fun `content text meets 4 point 5 to 1 on every skin`() {
        Skins.all.forEach { assertContrast(it, "content", ContrastRatio.of(it.content, it.background), BODY_TEXT) }
    }

    @Test
    fun `muted text meets 4 point 5 to 1 on every skin`() {
        Skins.all.forEach { assertContrast(it, "muted", ContrastRatio.of(it.mutedContent, it.background), BODY_TEXT) }
    }

    @Test
    fun `headlines in content colour meet 3 to 1 on every skin`() {
        Skins.all.forEach { assertContrast(it, "headline", ContrastRatio.of(it.content, it.background), LARGE_TEXT) }
    }

    @Test
    fun `white on black has the maximum contrast of 21 to 1`() {
        val ratio = ContrastRatio.of(MarginColors.White, Color.Black)
        assertTrue(ratio in 20.99..21.01, "was $ratio")
    }

    private fun assertContrast(skin: Skin, role: String, ratio: Double, minimum: Double) {
        assertTrue(ratio >= minimum, "${skin.name} $role contrast is ${"%.2f".format(ratio)}, below $minimum")
    }

    private companion object {
        const val BODY_TEXT = 4.5
        const val LARGE_TEXT = 3.0
    }
}
