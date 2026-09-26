package com.paulchibamba.margin.domain.feed

import com.paulchibamba.margin.domain.model.ConceptId
import com.paulchibamba.margin.domain.model.Format
import com.paulchibamba.margin.domain.model.PostId
import com.paulchibamba.margin.domain.model.PostRole

data class FeedHistoryEntry(
    val step: Int,
    val postId: PostId,
    val conceptId: ConceptId,
    val format: Format,
    val role: PostRole,
)
