package com.paulchibamba.margin.feature.feed.post

import com.paulchibamba.margin.domain.memory.Rating
import kotlin.time.Duration

data class TestPostState(
    val response: TestResponse? = null,
    val intervals: Map<Rating, Duration> = emptyMap(),
) {
    val isAnswered: Boolean
        get() = response != null

    val chosenIndex: Int?
        get() = (response as? TestResponse.Choice)?.index

    val saysTrue: Boolean?
        get() = (response as? TestResponse.Verdict)?.saysTrue

    val selfGrade: Rating?
        get() = (response as? TestResponse.SelfGrade)?.rating
}
