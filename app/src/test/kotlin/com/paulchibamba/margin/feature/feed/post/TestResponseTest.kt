package com.paulchibamba.margin.feature.feed.post

import com.paulchibamba.margin.domain.memory.Rating
import com.paulchibamba.margin.domain.model.PostContent
import com.paulchibamba.margin.domain.signals.AnswerOutcome
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class TestResponseTest {

    private val mcq = PostContent.Mcq("Quiz", "Which?", listOf("A", "B", "C", "D"), 2, "Because C.")
    private val statement = PostContent.TrueFalse("T/F", "Lax cookies go on cross-site POSTs.", false, "They don't.")

    @Test
    fun `picking the answer option is correct and any other is wrong`() {
        assertEquals(AnswerOutcome.Correct, TestResponse.Choice(2).outcomeFor(mcq))
        assertEquals(AnswerOutcome.Wrong, TestResponse.Choice(0).outcomeFor(mcq))
    }

    @Test
    fun `a verdict is correct when it matches the statement`() {
        assertEquals(AnswerOutcome.Correct, TestResponse.Verdict(saysTrue = false).outcomeFor(statement))
        assertEquals(AnswerOutcome.Wrong, TestResponse.Verdict(saysTrue = true).outcomeFor(statement))
    }

    @Test
    fun `a self-grade keeps its rating`() {
        assertEquals(AnswerOutcome.SelfGraded(Rating.HARD), TestResponse.SelfGrade(Rating.HARD).outcomeFor(mcq))
    }

    @Test
    fun `a response for another format has no outcome`() {
        assertNull(TestResponse.Verdict(saysTrue = true).outcomeFor(mcq))
        assertNull(TestResponse.Choice(0).outcomeFor(statement))
    }
}
