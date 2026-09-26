package com.paulchibamba.margin.domain.feed

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class LearningLibraryTest {

    @Test
    fun `concepts come back in book order whatever order they arrive in`() {
        assertEquals(
            listOf(cia, leastPrivilege, readingOnlyIntro, defenceInDepth, sameOrigin, promptInjection),
            libraryWith().conceptsInBookOrder,
        )
    }

    @Test
    fun `only books switched on in settings are active`() {
        assertEquals(listOf(appSec, grokking), libraryWith().activeBooks)
    }

    @Test
    fun `a concept without its source note has no book's-words post`() {
        assertNull(libraryWith().sourcePostFor(cia))
    }

    @Test
    fun `the book's words are capped at ninety words`() {
        val longNote = sourceNoteOf(cia, "<p>${List(200) { "word" }.joinToString(" ")}</p>")

        val source = libraryWith(sourceNotes = listOf(longNote)).sourcePostFor(cia)!!

        assertEquals(90, source.content.wordCount() - source.content.title.split(" ").size)
    }
}
