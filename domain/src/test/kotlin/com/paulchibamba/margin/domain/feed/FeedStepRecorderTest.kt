package com.paulchibamba.margin.domain.feed

import com.paulchibamba.margin.domain.memory.CardState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class FeedStepRecorderTest {

    private val recorder = FeedStepRecorder(DelightSchedule(5..9, FixedRandom(offset = 2)))

    @Test
    fun `showing a new concept introduces it with a new card due now`() {
        val state = recorder.record(freshState.copy(step = 3), Candidate(tipOf(cia), CandidateSource.NEW), now)

        val progress = state.progressOf(cia)
        assertEquals(4, progress.introducedAtStep)
        assertEquals(CardState.NEW, progress.card?.state)
        assertEquals(now, progress.card?.due)
        assertEquals(4, state.bookLastNewStep[appSec.slug])
    }

    @Test
    fun `a preview does not introduce its concept`() {
        val state = recorder.record(freshState, Candidate(tipOf(cia), CandidateSource.PREVIEW), now)

        assertNull(state.progressOf(cia).introducedAtStep)
        assertEquals(1, state.lastPreviewAtStep)
        assertEquals(1, state.progressOf(cia).lastShownStep)
    }

    @Test
    fun `a meme schedules the next one five to nine posts later`() {
        val state = recorder.record(freshState.copy(step = 9), Candidate(memeOf(cia), CandidateSource.DELIGHT), now)

        assertEquals(10 + 5 + 2, state.delightAtStep)
    }

    @Test
    fun `a teach post re-teaches a lost concept, a test does not`() {
        val lostProgress = introducedProgress().copy(confidence = Confidence.LOST, lostAtStep = 1)
        val lost = freshState.withIntroduced(cia, progress = lostProgress)

        val afterExplanation = recorder.record(lost, Candidate(mythOf(cia), CandidateSource.ANGLE), now)
        val afterQuiz = recorder.record(lost, Candidate(mcqOf(cia), CandidateSource.REVIEW), now)

        assertEquals(1, afterExplanation.progressOf(cia).retaughtAtStep)
        assertNull(afterQuiz.progressOf(cia).retaughtAtStep)
    }

    @Test
    fun `every shown post advances the step and is remembered in history`() {
        val state = recorder.record(freshState, Candidate(mcqOf(cia), CandidateSource.REVIEW), now)

        assertEquals(1, state.step)
        assertEquals(1, state.seenPosts[mcqOf(cia).id])
        val quiz = mcqOf(cia)
        assertEquals(FeedHistoryEntry(1, quiz.id, cia.id, quiz.format, quiz.role), state.history.single())
    }
}
