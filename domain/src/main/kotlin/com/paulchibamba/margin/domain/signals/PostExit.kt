package com.paulchibamba.margin.domain.signals

import kotlin.time.Duration

data class PostExit(
    val dwell: Duration,
    val isEngaged: Boolean,
    val answer: AnswerOutcome? = null,
)
