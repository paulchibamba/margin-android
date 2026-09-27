package com.paulchibamba.margin.domain.signals

import com.paulchibamba.margin.domain.memory.Rating
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class GradeMapperTest {

    private val mapper = GradeMapper()
    private val expected = 5.seconds

    @Test
    fun `a wrong answer is graded again`() {
        assertEquals(Rating.AGAIN, mapper.gradeFor(answered(AnswerOutcome.Wrong, 3.seconds), expected))
    }

    @Test
    fun `a right answer at normal speed is graded good`() {
        assertEquals(Rating.GOOD, mapper.gradeFor(answered(AnswerOutcome.Correct, 8.seconds), expected))
    }

    @Test
    fun `a right answer that takes exactly twice the reading time is still good`() {
        assertEquals(Rating.GOOD, mapper.gradeFor(answered(AnswerOutcome.Correct, 10.seconds), expected))
    }

    @Test
    fun `a right answer that takes more than twice the reading time is graded hard`() {
        assertEquals(Rating.HARD, mapper.gradeFor(answered(AnswerOutcome.Correct, 11.seconds), expected))
    }

    @Test
    fun `a right answer is never graded easy, however fast`() {
        assertEquals(Rating.GOOD, mapper.gradeFor(answered(AnswerOutcome.Correct, 0.5.seconds), expected))
    }

    @Test
    fun `a self-grade is taken as given`() {
        val exit = answered(AnswerOutcome.SelfGraded(Rating.HARD), 20.seconds)

        assertEquals(Rating.HARD, mapper.gradeFor(exit, expected))
    }

    @Test
    fun `a right answer is graded by the time it took to answer, not the time on the page`() {
        val exit = answered(AnswerOutcome.Correct, 40.seconds).copy(timeToAnswer = 4.seconds)

        assertEquals(Rating.GOOD, mapper.gradeFor(exit, expected))
    }

    @Test
    fun `a right answer that took more than twice the reading time to give is hard however soon the page is left`() {
        val exit = answered(AnswerOutcome.Correct, 12.seconds).copy(timeToAnswer = 11.seconds)

        assertEquals(Rating.HARD, mapper.gradeFor(exit, expected))
    }

    @Test
    fun `leaving without answering gives no grade`() {
        assertNull(mapper.gradeFor(PostExit(dwell = 3.seconds, isEngaged = false), expected))
    }

    private fun answered(answer: AnswerOutcome, dwell: Duration) =
        PostExit(dwell = dwell, isEngaged = true, answer = answer)
}
