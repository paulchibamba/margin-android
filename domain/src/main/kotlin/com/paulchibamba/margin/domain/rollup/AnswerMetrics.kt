package com.paulchibamba.margin.domain.rollup

import kotlin.time.Duration

data class AnswerMetrics(val answers: Int, val correct: Int, val medianTimeToAnswer: Duration?) {
    val accuracy: Double?
        get() = Share.of(correct, answers)
}
