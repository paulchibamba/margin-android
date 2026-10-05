package com.paulchibamba.margin.domain.feed

import com.paulchibamba.margin.domain.feed.ranking.ScoreBreakdown
import com.paulchibamba.margin.domain.model.Post

data class FeedItem(
    val post: Post,
    val source: CandidateSource,
    val score: ScoreBreakdown,
    val rank: Int,
    val poolSize: Int,
    val wasExploration: Boolean,
    val appliedFilters: List<String>,
    val memory: MemorySnapshot?,
    val step: Int,
)
