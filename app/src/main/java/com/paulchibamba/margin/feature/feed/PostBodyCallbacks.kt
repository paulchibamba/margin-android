package com.paulchibamba.margin.feature.feed

class PostBodyCallbacks(
    val onReadSource: () -> Unit = {},
    val onReadAhead: () -> Unit = {},
    val onEngaged: () -> Unit = {},
)
