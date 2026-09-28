package com.paulchibamba.margin.feature.read.note

import org.junit.Test
import kotlin.test.assertEquals

class NoteTextZoomTest {

    @Test
    fun `the note text follows the system font size`() {
        assertEquals(100, textZoomFor(1f))
        assertEquals(130, textZoomFor(1.3f))
        assertEquals(200, textZoomFor(2f))
    }
}
