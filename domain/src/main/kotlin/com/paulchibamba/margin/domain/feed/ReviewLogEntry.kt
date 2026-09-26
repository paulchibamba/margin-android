package com.paulchibamba.margin.domain.feed

import com.paulchibamba.margin.domain.memory.CardState
import com.paulchibamba.margin.domain.memory.Rating
import com.paulchibamba.margin.domain.model.ConceptId
import com.paulchibamba.margin.domain.model.PostId
import java.time.Instant
import kotlin.time.Duration

data class ReviewLogEntry(
    val at: Instant,
    val conceptId: ConceptId,
    val postId: PostId,
    val rating: Rating,
    val stateBefore: CardState,
    val stabilityBefore: Double,
    val stabilityAfter: Double,
    val dwell: Duration?,
)
