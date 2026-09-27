package com.paulchibamba.margin.feature.feed

import com.paulchibamba.margin.domain.memory.Rating
import com.paulchibamba.margin.domain.signals.AnswerOutcome
import com.paulchibamba.margin.feature.feed.post.TestResponse
import kotlin.time.Duration

data class TestAnswer(
    val response: TestResponse,
    val outcome: AnswerOutcome,
    val timeToAnswer: Duration,
    val rating: Rating,
)
