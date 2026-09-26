package com.paulchibamba.margin.domain.feed.filter

import com.paulchibamba.margin.domain.feed.Candidate
import com.paulchibamba.margin.domain.feed.CandidateSource
import com.paulchibamba.margin.domain.feed.Confidence
import com.paulchibamba.margin.domain.feed.FeedState

class NoConceptRepeat(private val spacing: Int) : FeedFilter {
    override val name = "concept spacing"

    override fun keeps(candidate: Candidate, state: FeedState): Boolean {
        val concept = candidate.post.conceptId
        val recentConcepts = state.history.takeLast(spacing).map { it.conceptId }
        return concept !in recentConcepts || isReteachAfterAGap(candidate, state)
    }

    private fun isReteachAfterAGap(candidate: Candidate, state: FeedState): Boolean {
        val concept = candidate.post.conceptId
        val isLost = state.progressOf(concept).confidence == Confidence.LOST
        val wasJustShown = state.history.lastOrNull()?.conceptId == concept
        return candidate.source == CandidateSource.ANGLE && isLost && !wasJustShown
    }
}
