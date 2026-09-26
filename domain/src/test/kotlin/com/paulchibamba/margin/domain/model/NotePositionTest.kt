package com.paulchibamba.margin.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NotePositionTest {

    @Test
    fun `a later note in the same chapter comes after an earlier one`() {
        assertTrue(NotePosition(chapter = 3, order = 2) < NotePosition(chapter = 3, order = 7))
    }

    @Test
    fun `any note in a later chapter comes after every note in an earlier one`() {
        assertTrue(NotePosition(chapter = 2, order = 40) < NotePosition(chapter = 3, order = 0))
    }

    @Test
    fun `positions with the same chapter and order are equal`() {
        assertEquals(0, NotePosition(chapter = 5, order = 1).compareTo(NotePosition(chapter = 5, order = 1)))
    }

    @Test
    fun `sorting puts positions in book order`() {
        val shuffled = listOf(NotePosition(2, 0), NotePosition(1, 9), NotePosition(1, 3))

        assertEquals(listOf(NotePosition(1, 3), NotePosition(1, 9), NotePosition(2, 0)), shuffled.sorted())
    }
}
