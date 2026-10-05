package com.paulchibamba.margin.domain.signals

import com.paulchibamba.margin.domain.memory.Rating

sealed interface AnswerOutcome {
    val isCorrect: Boolean?
        get() = when (this) {
            Correct -> true
            Wrong -> false
            is SelfGraded -> null
        }

    data object Correct : AnswerOutcome
    data object Wrong : AnswerOutcome
    data class SelfGraded(val rating: Rating) : AnswerOutcome
}
