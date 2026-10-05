package com.paulchibamba.margin.domain.progress

import com.paulchibamba.margin.domain.actions.ActionLogEntry
import com.paulchibamba.margin.domain.feed.ReviewLogEntry
import com.paulchibamba.margin.domain.model.Concept
import com.paulchibamba.margin.domain.model.ConceptId
import com.paulchibamba.margin.domain.model.NoteId

internal class LoggedStruggleInputs(
    concepts: List<Concept>,
    override val reviews: List<ReviewLogEntry>,
    override val actions: List<ActionLogEntry>,
) : StruggleInputs {
    private val conceptsById = concepts.associateBy(Concept::id)
    private val conceptsByNote = concepts.filter { it.sourceNoteId != null }.groupBy { it.sourceNoteId!! }

    override fun conceptOf(id: ConceptId): Concept? = conceptsById[id]

    override fun conceptsOnNote(note: NoteId): List<Concept> = conceptsByNote[note].orEmpty()

    override fun isIntroduced(concept: Concept): Boolean = true
}
