package com.paulchibamba.margin.designsystem

import org.junit.Test
import kotlin.test.assertEquals

class FontScaleCapTest {

    @Test
    fun `chrome labels follow the font size up to the cap`() {
        assertEquals(1f, shrinkFor(fontScale = 1f, cap = 1.3f))
        assertEquals(1f, shrinkFor(fontScale = 1.3f, cap = 1.3f))
    }

    @Test
    fun `above the cap chrome labels shrink back to the cap`() {
        assertEquals(0.65f, shrinkFor(fontScale = 2f, cap = 1.3f), absoluteTolerance = 0.001f)
    }
}
