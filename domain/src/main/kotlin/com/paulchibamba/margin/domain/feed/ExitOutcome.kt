package com.paulchibamba.margin.domain.feed

import com.paulchibamba.margin.domain.memory.Rating

data class ExitOutcome(
    val state: FeedState,
    val engagement: Double,
    val grade: Rating?,
    val reviewLogEntry: ReviewLogEntry?,
)
