package com.paulchibamba.margin.domain.progress

import com.paulchibamba.margin.domain.model.ConceptId
import com.paulchibamba.margin.domain.model.NoteId
import java.time.Instant

data class PastReward(
    val kind: RewardKind,
    val conceptIds: List<ConceptId>,
    val noteId: NoteId? = null,
    val facts: Map<String, String>,
    val title: String,
    val createdAt: Instant,
    val shownAt: Instant? = null,
)
