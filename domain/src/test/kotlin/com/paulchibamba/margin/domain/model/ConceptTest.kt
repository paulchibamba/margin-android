package com.paulchibamba.margin.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals

class ConceptTest {

    @Test
    fun `a concept sits at the position of its source note`() {
        val concept = conceptWith(sourceNoteId = NoteId("alice-bob-appsec/ch04/n017"))

        assertEquals(NotePosition(chapter = 4, order = 17), concept.position)
    }

    @Test
    fun `a concept without a source note sits at its own chapter and order`() {
        val concept = conceptWith(sourceNoteId = null)

        assertEquals(NotePosition(chapter = 2, order = 5), concept.position)
    }

    private fun conceptWith(sourceNoteId: NoteId?) = Concept(
        id = ConceptId("alice-bob-appsec/ch02/3fa9c01e"),
        bookSlug = BookSlug("alice-bob-appsec"),
        chapter = 2,
        order = 5,
        title = "Least privilege",
        summary = "Grant only the access a task needs.",
        section = "Least Privilege",
        sourceNoteId = sourceNoteId,
    )
}
