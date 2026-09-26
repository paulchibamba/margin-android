package com.paulchibamba.margin.domain.feed.ranking

data class Pick(val chosen: ScoredCandidate, val rank: Int, val wasExploration: Boolean)
