package com.paulchibamba.margin.domain.feed

import com.paulchibamba.margin.domain.memory.CardState
import com.paulchibamba.margin.domain.memory.Rating
import com.paulchibamba.margin.domain.model.Format
import com.paulchibamba.margin.domain.model.Post
import com.paulchibamba.margin.domain.model.PostContent
import com.paulchibamba.margin.domain.model.PostId
import com.paulchibamba.margin.domain.signals.AnswerOutcome
import com.paulchibamba.margin.domain.signals.EngagementCalculator
import com.paulchibamba.margin.domain.signals.GradeMapper
import com.paulchibamba.margin.domain.signals.PostExit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class PostExitHandlerTest {

    private val handler = PostExitHandler(EngagementCalculator(), GradeMapper(), testScheduler)
    private val learning = freshState.withIntroduced(cia, progress = introducedProgress(due = now))
    private val lostAndGraded = freshState.withIntroduced(
        cia,
        progress = learnedProgress().copy(confidence = Confidence.LOST, lostAtStep = 1, isLostGraded = true),
    )

    private fun exitAfter(answer: AnswerOutcome?, dwell: Duration = 5.seconds) =
        PostExit(dwell = dwell, isEngaged = true, answer = answer)

    @Test
    fun `a fast skip on a checklist moves its affinity from 0·5 to 0·375`() {
        val checklist = Post(PostId("cia/checklist"), cia.id, cia.bookSlug, PostContent.Checklist("C", listOf("a")))

        val outcome = handler.apply(learning, checklist, PostExit(500.milliseconds, isEngaged = false), now)

        assertEquals(0.0, outcome.engagement)
        assertEquals(0.375, outcome.state.affinity.valueOf(Format.CHECKLIST), TOLERANCE)
    }

    @Test
    fun `exit on a teach post never grades`() {
        val outcome = handler.apply(learning, mythOf(cia), exitAfter(AnswerOutcome.Wrong), now)

        assertNull(outcome.grade)
        assertNull(outcome.reviewLogEntry)
        assertEquals(learning.progressOf(cia), outcome.state.progressOf(cia))
    }

    @Test
    fun `a test left without an answer is not graded`() {
        val outcome = handler.apply(learning, mcqOf(cia), exitAfter(answer = null), now)

        assertNull(outcome.grade)
        assertEquals(learning.progressOf(cia), outcome.state.progressOf(cia))
    }

    @Test
    fun `a test of a concept with no card is not graded`() {
        val outcome = handler.apply(freshState, mcqOf(cia), exitAfter(AnswerOutcome.Correct), now)

        assertNull(outcome.grade)
        assertNull(outcome.reviewLogEntry)
    }

    @Test
    fun `a correct answer grades the concept's card Good and logs the review`() {
        val outcome = handler.apply(learning, mcqOf(cia), exitAfter(AnswerOutcome.Correct), now)

        assertEquals(Rating.GOOD, outcome.grade)
        assertEquals(CardState.LEARNING, outcome.state.progressOf(cia).card?.state)
        val entry = outcome.reviewLogEntry
        assertEquals(ReviewLogEntry(now, cia.id, mcqOf(cia).id, Rating.GOOD, CardState.NEW, 0.0,
            outcome.state.progressOf(cia).card!!.stability, 5.seconds), entry)
    }

    @Test
    fun `a slow correct answer grades Hard and a self-grade is taken as given`() {
        val slow = handler.apply(learning, mcqOf(cia), exitAfter(AnswerOutcome.Correct, dwell = 60.seconds), now)
        val selfGraded = handler.apply(learning, mcqOf(cia), exitAfter(AnswerOutcome.SelfGraded(Rating.HARD)), now)

        assertEquals(Rating.HARD, slow.grade)
        assertEquals(Rating.HARD, selfGraded.grade)
    }

    @Test
    fun `a correct answer after Lost clears LOST and resets the lapse flag`() {
        val outcome = handler.apply(lostAndGraded, mcqOf(cia), exitAfter(AnswerOutcome.Correct), now)

        assertNull(outcome.state.progressOf(cia).confidence)
        assertFalse(outcome.state.progressOf(cia).isLostGraded)
    }

    @Test
    fun `a wrong answer after Lost keeps LOST`() {
        val outcome = handler.apply(lostAndGraded, mcqOf(cia), exitAfter(AnswerOutcome.Wrong), now)

        assertEquals(Rating.AGAIN, outcome.grade)
        assertEquals(Confidence.LOST, outcome.state.progressOf(cia).confidence)
    }

    @Test
    fun `leaving a post marked Less scores no engagement and keeps the halved affinity`() {
        val halved = learning.copy(affinity = learning.affinity.afterLess(Format.TIP))

        val outcome = handler.apply(halved, tipOf(cia), exitAfter(null).copy(isMarkedLess = true), now)

        assertEquals(0.0, outcome.engagement)
        assertEquals(0.25, outcome.state.affinity.valueOf(Format.TIP), TOLERANCE)
    }

    @Test
    fun `a read post moves affinity toward its engagement`() {
        val outcome = handler.apply(learning, tipOf(cia), exitAfter(null, dwell = 12.seconds), now)

        assertEquals(1.0, outcome.engagement)
        assertEquals(0.5 + 0.25 * 0.5, outcome.state.affinity.valueOf(Format.TIP), TOLERANCE)
    }

    private companion object {
        const val TOLERANCE = 1e-9
    }
}
