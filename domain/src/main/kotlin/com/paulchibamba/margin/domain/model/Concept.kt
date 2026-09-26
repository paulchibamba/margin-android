package com.paulchibamba.margin.domain.model

data class Concept(
    val id: ConceptId,
    val bookSlug: BookSlug,
    val chapter: Int,
    val order: Int,
    val title: String,
    val summary: String,
    val section: String,
    val sourceNoteId: NoteId?,
) {
    val position: NotePosition
        get() = sourceNoteId?.position() ?: NotePosition(chapter, order)
}
