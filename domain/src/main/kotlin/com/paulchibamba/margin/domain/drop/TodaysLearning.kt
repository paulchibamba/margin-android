package com.paulchibamba.margin.domain.drop

import com.paulchibamba.margin.domain.feed.FeedState
import com.paulchibamba.margin.domain.feed.ReviewLogEntry

data class TodaysLearning(
    val state: FeedState,
    val reviews: List<ReviewLogEntry>,
    val introduced: Int,
    val postsSeen: Int,
)
