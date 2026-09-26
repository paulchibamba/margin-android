package com.paulchibamba.margin.domain.actions

import com.paulchibamba.margin.domain.model.ConceptId
import com.paulchibamba.margin.domain.model.PostId
import java.time.Instant

data class ActionLogEntry(
    val at: Instant,
    val step: Int,
    val postId: PostId,
    val conceptId: ConceptId,
    val action: PostAction,
)
