package com.paulchibamba.margin.domain.feed.filter

import com.paulchibamba.margin.domain.feed.Candidate
import com.paulchibamba.margin.domain.feed.FeedState
import com.paulchibamba.margin.domain.model.PostRole

class TestStreakCap(private val maxTestsInRow: Int) : FeedFilter {
    override val name = "tests in a row"

    override fun keeps(candidate: Candidate, state: FeedState): Boolean =
        candidate.post.role != PostRole.TEST || !isOnTestStreak(state)

    private fun isOnTestStreak(state: FeedState): Boolean {
        val recent = state.history.takeLast(maxTestsInRow)
        return recent.size == maxTestsInRow && recent.all { it.role == PostRole.TEST }
    }
}
