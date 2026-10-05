package com.paulchibamba.margin.feature.feed

import com.paulchibamba.margin.domain.tracking.InteractionKind
import com.paulchibamba.margin.feature.feed.post.TestResponse

class PostBodyCallbacks(
    val onReadSource: () -> Unit = {},
    val onReadAhead: () -> Unit = {},
    val onEngaged: () -> Unit = {},
    val onRespond: (TestResponse) -> Unit = {},
    val onInteraction: (InteractionKind) -> Unit = {},
) {
    fun engagedBy(kind: InteractionKind): () -> Unit = {
        onEngaged()
        onInteraction(kind)
    }
}
