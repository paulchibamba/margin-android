package com.paulchibamba.margin.feature.feed

import com.paulchibamba.margin.feature.feed.post.TestResponse

class PostBodyCallbacks(
    val onReadSource: () -> Unit = {},
    val onReadAhead: () -> Unit = {},
    val onEngaged: () -> Unit = {},
    val onRespond: (TestResponse) -> Unit = {},
)
