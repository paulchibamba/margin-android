package com.paulchibamba.margin.domain.feed.ranking

import com.paulchibamba.margin.domain.feed.Candidate

data class ScoredCandidate(val candidate: Candidate, val score: ScoreBreakdown)
