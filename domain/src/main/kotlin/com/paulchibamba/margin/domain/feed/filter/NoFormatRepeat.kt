package com.paulchibamba.margin.domain.feed.filter

import com.paulchibamba.margin.domain.feed.Candidate
import com.paulchibamba.margin.domain.feed.FeedState

class NoFormatRepeat : FeedFilter {
    override val name = "format variety"

    override fun keeps(candidate: Candidate, state: FeedState): Boolean =
        candidate.post.format != state.history.lastOrNull()?.format
}
