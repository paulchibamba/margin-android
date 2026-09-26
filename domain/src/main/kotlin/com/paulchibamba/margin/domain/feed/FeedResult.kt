package com.paulchibamba.margin.domain.feed

sealed interface FeedResult {
    data class Next(val item: FeedItem, val state: FeedState) : FeedResult
    data object CaughtUp : FeedResult
}
