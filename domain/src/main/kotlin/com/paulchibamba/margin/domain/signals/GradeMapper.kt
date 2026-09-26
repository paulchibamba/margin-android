package com.paulchibamba.margin.domain.signals

import com.paulchibamba.margin.domain.memory.Rating
import kotlin.time.Duration

class GradeMapper(private val hardFactor: Double = DEFAULT_HARD_FACTOR) {

    fun gradeFor(exit: PostExit, expected: Duration): Rating? = when (val answer = exit.answer) {
        null -> null
        is AnswerOutcome.SelfGraded -> answer.rating
        AnswerOutcome.Wrong -> Rating.AGAIN
        AnswerOutcome.Correct -> if (isSlow(exit.dwell, expected)) Rating.HARD else Rating.GOOD
    }

    private fun isSlow(dwell: Duration, expected: Duration): Boolean = dwell > expected * hardFactor

    companion object {
        const val DEFAULT_HARD_FACTOR = 2.0
    }
}
