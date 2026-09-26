package com.paulchibamba.margin.domain.feed.filter

import com.paulchibamba.margin.domain.feed.Candidate
import com.paulchibamba.margin.domain.feed.FeedState

class NotRecentlyShown(private val window: Int) : FeedFilter {
    override val name = "not shown recently"

    override fun keeps(candidate: Candidate, state: FeedState): Boolean {
        val shownAt = state.seenPosts[candidate.post.id] ?: return true
        return state.step - shownAt >= window
    }
}
