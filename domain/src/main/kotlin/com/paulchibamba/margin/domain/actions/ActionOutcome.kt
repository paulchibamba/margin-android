package com.paulchibamba.margin.domain.actions

import com.paulchibamba.margin.domain.feed.FeedState
import com.paulchibamba.margin.domain.feed.ReviewLogEntry

data class ActionOutcome(
    val state: FeedState,
    val nudge: Nudge?,
    val logEntry: ActionLogEntry,
    val reviewLogEntry: ReviewLogEntry? = null,
)
