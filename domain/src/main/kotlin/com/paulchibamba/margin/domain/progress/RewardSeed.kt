package com.paulchibamba.margin.domain.progress

import com.paulchibamba.margin.domain.model.ConceptId
import com.paulchibamba.margin.domain.model.NoteId

data class RewardSeed(
    val kind: RewardKind,
    val conceptIds: List<ConceptId>,
    val noteId: NoteId? = null,
    val facts: Map<String, String>,
) {
    val factsHash: String = FactsHash.of(kind, conceptIds, facts)
}
