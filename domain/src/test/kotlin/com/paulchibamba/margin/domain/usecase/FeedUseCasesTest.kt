package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.actions.PostAction
import com.paulchibamba.margin.domain.feed.CandidateSource
import com.paulchibamba.margin.domain.feed.Confidence
import com.paulchibamba.margin.domain.feed.FeedResult
import com.paulchibamba.margin.domain.feed.appSec
import com.paulchibamba.margin.domain.feed.cia
import com.paulchibamba.margin.domain.feed.learnedProgress
import com.paulchibamba.margin.domain.feed.mcqOf
import com.paulchibamba.margin.domain.feed.noteId
import com.paulchibamba.margin.domain.feed.tipOf
import com.paulchibamba.margin.domain.feed.withIntroduced
import com.paulchibamba.margin.domain.memory.Rating
import com.paulchibamba.margin.domain.model.ChapterRef
import com.paulchibamba.margin.domain.signals.AnswerOutcome
import com.paulchibamba.margin.domain.signals.PostExit
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds

class FeedUseCasesTest {

    private val fixture = UseCaseFixture()

    @Test
    fun `the first post starts a feed state and saves it`() = runTest {
        val result = assertIs<FeedResult.Next>(fixture.getNextPost())

        assertEquals(CandidateSource.PREVIEW, result.item.source)
        assertEquals(1, fixture.progress.feedState.value?.step)
    }

    @Test
    fun `each next post continues from the saved state`() = runTest {
        fixture.getNextPost()
        fixture.getNextPost()

        assertEquals(2, fixture.progress.feedState.value?.step)
    }

    @Test
    fun `knowing a chapter unlocks its concepts as new posts`() = runTest {
        fixture.markChapterKnown(ChapterRef(appSec.slug, 1))
        fixture.progress.feedState.value = fixture.engines.feedEngine(0.9).startingState().copy(lastPreviewAtStep = 0)

        val item = assertIs<FeedResult.Next>(fixture.getNextPost()).item

        assertEquals(CandidateSource.NEW, item.source)
    }

    @Test
    fun `with no active book and nothing introduced the feed is caught up`() = runTest {
        fixture.settings.bookSettings.value = fixture.settings.bookSettings.value.map { it.copy(isActive = false) }

        assertEquals(FeedResult.CaughtUp, fixture.getNextPost())
    }

    @Test
    fun `leaving a test grades it, logs the review and counts the post for today`() = runTest {
        fixture.progress.feedState.value = startingState().withIntroduced(cia, progress = learnedProgress())

        val recorded = fixture.recordPostExit(mcqOf(cia), PostExit(5.seconds, true, AnswerOutcome.Correct))

        assertEquals(Rating.GOOD, recorded.outcome.grade)
        assertEquals(Rating.GOOD, fixture.progress.reviews.value.single().rating)
        assertEquals(1, fixture.progress.activity.value.single().postsSeen)
        assertEquals(listOf(mcqOf(cia).id to recorded.outcome.engagement), fixture.progress.exits)
    }

    @Test
    fun `the fifth post of the day extends the streak`() = runTest {
        val extended = (1..5).map { fixture.recordPostExit(tipOf(cia), PostExit(5.seconds, true)).isStreakExtended }

        assertEquals(listOf(false, false, false, false, true), extended)
    }

    @Test
    fun `an action is applied, saved and logged`() = runTest {
        fixture.progress.feedState.value = startingState().withIntroduced(cia, progress = learnedProgress())

        fixture.applyPostAction(tipOf(cia), PostAction.LOST)

        assertEquals(Confidence.LOST, fixture.progress.feedState.value?.progressOf(cia)?.confidence)
        assertEquals(PostAction.LOST, fixture.progress.actions.value.single().action)
        assertEquals(Rating.AGAIN, fixture.progress.reviews.value.single().rating)
    }

    @Test
    fun `reading a note for the first time counts it and extends the streak`() = runTest {
        val note = noteId(appSec, 1, 0)

        val first = fixture.markNoteRead(note)
        val again = fixture.markNoteRead(note)

        assertTrue(first.isNewlyRead && first.isStreakExtended)
        assertFalse(again.isNewlyRead || again.isStreakExtended)
        assertEquals(1, fixture.progress.activity.value.single().notesRead)
    }

    private fun startingState() = fixture.engines.feedEngine(0.9).startingState()
}
