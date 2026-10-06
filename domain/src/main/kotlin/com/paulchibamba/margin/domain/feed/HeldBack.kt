package com.paulchibamba.margin.domain.feed

import com.paulchibamba.margin.domain.model.ConceptId
import com.paulchibamba.margin.domain.model.PostId

data class HeldBack(val posts: Set<PostId> = emptySet(), val reviewedConcepts: Set<ConceptId> = emptySet()) {

    fun holds(candidate: Candidate): Boolean = candidate.post.id in posts ||
        (candidate.source == CandidateSource.REVIEW && candidate.post.conceptId in reviewedConcepts)

    companion object {
        val Nothing = HeldBack()
    }
}
