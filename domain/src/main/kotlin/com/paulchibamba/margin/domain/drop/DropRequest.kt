package com.paulchibamba.margin.domain.drop

import com.paulchibamba.margin.domain.feed.FeedState
import com.paulchibamba.margin.domain.feed.LearningLibrary
import com.paulchibamba.margin.domain.model.Post
import com.paulchibamba.margin.domain.model.PostId
import java.time.Instant

data class DropRequest(
    val library: LearningLibrary,
    val state: FeedState,
    val now: Instant,
    val fallbackClosings: List<Post> = emptyList(),
    val headlinePost: PostId? = null,
)
