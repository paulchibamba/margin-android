package com.paulchibamba.margin.domain.progression

import com.paulchibamba.margin.domain.model.NotePosition
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PreviewWindowTest {

    private val window = PreviewWindow(notesAhead = 4)
    private val frontier = NotePosition(chapter = 4, order = 10)

    @Test
    fun `with nothing read, the first four notes of a chapter are in the window`() {
        assertTrue(window.contains(NotePosition(chapter = 1, order = 3), frontier = null))
        assertFalse(window.contains(NotePosition(chapter = 1, order = 4), frontier = null))
    }

    @Test
    fun `with nothing read, the start of any chapter counts, as in the prototype`() {
        assertTrue(window.contains(NotePosition(chapter = 3, order = 0), frontier = null))
    }

    @Test
    fun `notes at or before the frontier are not previews`() {
        assertFalse(window.contains(NotePosition(chapter = 4, order = 10), frontier))
        assertFalse(window.contains(NotePosition(chapter = 2, order = 1), frontier))
    }

    @Test
    fun `up to four notes past the frontier in the same chapter are in the window`() {
        assertTrue(window.contains(NotePosition(chapter = 4, order = 11), frontier))
        assertTrue(window.contains(NotePosition(chapter = 4, order = 14), frontier))
        assertFalse(window.contains(NotePosition(chapter = 4, order = 15), frontier))
    }

    @Test
    fun `the first four notes of the next chapter are in the window`() {
        assertTrue(window.contains(NotePosition(chapter = 5, order = 3), frontier))
        assertFalse(window.contains(NotePosition(chapter = 5, order = 4), frontier))
    }

    @Test
    fun `chapters further ahead are outside the window`() {
        assertFalse(window.contains(NotePosition(chapter = 6, order = 0), frontier))
    }
}
