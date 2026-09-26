package com.paulchibamba.margin.domain.feed.ranking

data class ScoreBreakdown(val parts: Map<ScorePart, Double>) {
    val total: Double = parts.values.sum()
}
