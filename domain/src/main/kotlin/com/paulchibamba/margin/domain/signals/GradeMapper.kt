package com.paulchibamba.margin.domain.signals

import com.paulchibamba.margin.domain.memory.Rating
import kotlin.time.Duration

class GradeMapper(private val hardFactor: Double = DEFAULT_HARD_FACTOR) {

    fun gradeFor(exit: PostExit, expected: Duration): Rating? =
        exit.answer?.let { answer -> gradeFor(answer, exit.timeToAnswer ?: exit.dwell, expected) }

    fun gradeFor(answer: AnswerOutcome, timeToAnswer: Duration, expected: Duration): Rating = when (answer) {
        is AnswerOutcome.SelfGraded -> answer.rating
        AnswerOutcome.Wrong -> Rating.AGAIN
        AnswerOutcome.Correct -> if (isSlow(timeToAnswer, expected)) Rating.HARD else Rating.GOOD
    }

    private fun isSlow(timeToAnswer: Duration, expected: Duration): Boolean = timeToAnswer > expected * hardFactor

    companion object {
        const val DEFAULT_HARD_FACTOR = 2.0
    }
}
