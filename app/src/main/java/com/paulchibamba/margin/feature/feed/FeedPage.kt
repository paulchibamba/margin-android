package com.paulchibamba.margin.feature.feed

import com.paulchibamba.margin.designsystem.Skin
import com.paulchibamba.margin.domain.actions.PostViewState
import com.paulchibamba.margin.domain.feed.FeedItem
import com.paulchibamba.margin.domain.usecase.PostContext

data class FeedPage(
    val item: FeedItem,
    val context: PostContext,
    val skin: Skin,
    val viewState: PostViewState = PostViewState(),
    val isExitRecorded: Boolean = false,
)
