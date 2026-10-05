package com.paulchibamba.margin.domain.rollup

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.seconds

class AnswerRollupTest {

    @Test
    fun `answers count a self-graded Again as wrong`() {
        val answers = AnswerRollup.of(RollupFixture.day)

        assertEquals(2, answers.answers)
        assertEquals(0.5, answers.accuracy)
        assertEquals(5.seconds, answers.medianTimeToAnswer)
    }
}
