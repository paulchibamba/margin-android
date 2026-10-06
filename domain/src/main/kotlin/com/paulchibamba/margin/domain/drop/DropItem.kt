package com.paulchibamba.margin.domain.drop

import com.paulchibamba.margin.domain.feed.CandidateSource
import com.paulchibamba.margin.domain.feed.FeedState
import com.paulchibamba.margin.domain.model.PostId

data class DropItem(
    val postId: PostId,
    val slot: DropSlot,
    val source: CandidateSource,
    val enteredAtStep: Int? = null,
) {
    fun isSeenOutsideDrop(state: FeedState, composedAtStep: Int): Boolean {
        val lastSeenStep = state.seenPosts[postId] ?: return false
        return lastSeenStep > (enteredAtStep ?: composedAtStep)
    }
}
