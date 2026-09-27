package com.paulchibamba.margin.feature.feed.post

import com.paulchibamba.margin.domain.memory.Rating
import com.paulchibamba.margin.domain.model.ChoiceQuestion
import com.paulchibamba.margin.domain.model.PostContent
import com.paulchibamba.margin.domain.signals.AnswerOutcome

sealed interface TestResponse {
    data class Choice(val index: Int) : TestResponse
    data class Verdict(val saysTrue: Boolean) : TestResponse
    data class SelfGrade(val rating: Rating) : TestResponse

    fun outcomeFor(content: PostContent): AnswerOutcome? = when (this) {
        is Choice -> (content as? ChoiceQuestion)?.let { question -> outcomeOf(question.isCorrect(index)) }
        is Verdict -> (content as? PostContent.TrueFalse)?.let { statement -> outcomeOf(saysTrue == statement.isTrue) }
        is SelfGrade -> AnswerOutcome.SelfGraded(rating)
    }

    private fun outcomeOf(isCorrect: Boolean): AnswerOutcome =
        if (isCorrect) AnswerOutcome.Correct else AnswerOutcome.Wrong
}
