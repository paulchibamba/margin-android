package com.paulchibamba.margin.domain.rollup

import com.paulchibamba.margin.domain.memory.Rating
import com.paulchibamba.margin.domain.tracking.Event
import kotlin.time.Duration.Companion.milliseconds

object AnswerRollup {

    fun of(day: DayEvents): AnswerMetrics {
        val answers = day.all<Event.PostAnswer>()
        return AnswerMetrics(
            answers = answers.size,
            correct = answers.count(::isCorrect),
            medianTimeToAnswer = Median.of(answers.map(::millisecondsToAnswer))?.milliseconds,
        )
    }

    private fun millisecondsToAnswer(answer: Event.PostAnswer): Double =
        answer.timeToAnswer.inWholeMilliseconds.toDouble()

    private fun isCorrect(answer: Event.PostAnswer): Boolean = answer.isCorrect ?: (answer.grade != Rating.AGAIN)
}
