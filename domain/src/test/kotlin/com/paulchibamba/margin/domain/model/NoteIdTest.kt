package com.paulchibamba.margin.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class NoteIdTest {

    @Test
    fun `a note id gives its chapter and order`() {
        assertEquals(NotePosition(chapter = 6, order = 12), NoteId("alice-bob-appsec/ch06/n012").position())
    }

    @Test
    fun `a note id gives the slug of its book`() {
        assertEquals(BookSlug("alice-bob-appsec"), NoteId("alice-bob-appsec/ch06/n012").bookSlug)
    }

    @Test
    fun `a note id without a chapter and note number has no position`() {
        assertNull(NoteId("alice-bob-appsec/intro").position())
    }

    @Test
    fun `an empty note id has no position`() {
        assertNull(NoteId("").position())
    }
}
