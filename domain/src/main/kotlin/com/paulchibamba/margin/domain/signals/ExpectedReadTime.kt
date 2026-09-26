package com.paulchibamba.margin.domain.signals

import com.paulchibamba.margin.domain.model.PostContent
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

object ExpectedReadTime {

    private val PER_WORD = 250.milliseconds
    private val SHORTEST = 2.5.seconds
    private val LONGEST = 12.seconds

    fun of(content: PostContent): Duration = (PER_WORD * content.wordCount()).coerceIn(SHORTEST, LONGEST)
}
