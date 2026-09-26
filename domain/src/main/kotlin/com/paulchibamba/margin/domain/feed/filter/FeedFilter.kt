package com.paulchibamba.margin.domain.feed.filter

import com.paulchibamba.margin.domain.feed.Candidate
import com.paulchibamba.margin.domain.feed.FeedState

interface FeedFilter {
    val name: String

    fun keeps(candidate: Candidate, state: FeedState): Boolean
}
