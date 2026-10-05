package com.paulchibamba.margin.domain.rollup

import com.paulchibamba.margin.domain.model.ConceptId
import com.paulchibamba.margin.domain.model.NoteId

sealed interface RereadSubject {
    data class OfConcept(val conceptId: ConceptId) : RereadSubject

    data class OfNote(val noteId: NoteId) : RereadSubject
}
