package com.paulchibamba.margin.domain.progress

import com.paulchibamba.margin.domain.actions.ActionLogEntry
import com.paulchibamba.margin.domain.feed.ReviewLogEntry
import com.paulchibamba.margin.domain.memory.Rating
import com.paulchibamba.margin.domain.model.Concept
import com.paulchibamba.margin.domain.model.ConceptId
import com.paulchibamba.margin.domain.model.NoteId
import java.time.Instant

internal interface StruggleInputs {
    val reviews: List<ReviewLogEntry>
    val actions: List<ActionLogEntry>

    fun conceptOf(id: ConceptId): Concept?

    fun conceptsOnNote(note: NoteId): List<Concept>

    fun isIntroduced(concept: Concept): Boolean

    fun hasPassedSince(concept: Concept, since: Instant): Boolean = reviews.any { review ->
        review.conceptId == concept.id && review.rating != Rating.AGAIN && review.at.isAfter(since)
    }
}
