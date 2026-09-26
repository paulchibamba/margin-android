package com.paulchibamba.margin.domain.actions

import com.paulchibamba.margin.domain.feed.CandidateSource
import com.paulchibamba.margin.domain.feed.Candidate
import com.paulchibamba.margin.domain.feed.Confidence
import com.paulchibamba.margin.domain.feed.FeedConfig
import com.paulchibamba.margin.domain.feed.FeedState
import com.paulchibamba.margin.domain.feed.FixedRandom
import com.paulchibamba.margin.domain.feed.RecallEstimate
import com.paulchibamba.margin.domain.feed.cia
import com.paulchibamba.margin.domain.feed.freshState
import com.paulchibamba.margin.domain.feed.introducedProgress
import com.paulchibamba.margin.domain.feed.learnedProgress
import com.paulchibamba.margin.domain.feed.libraryWith
import com.paulchibamba.margin.domain.feed.mcqOf
import com.paulchibamba.margin.domain.feed.mythOf
import com.paulchibamba.margin.domain.feed.now
import com.paulchibamba.margin.domain.feed.ranking.CandidateScorer
import com.paulchibamba.margin.domain.feed.ranking.ScorePart
import com.paulchibamba.margin.domain.feed.readingEverything
import com.paulchibamba.margin.domain.feed.source.AngleProvider
import com.paulchibamba.margin.domain.feed.source.ReviewProvider
import com.paulchibamba.margin.domain.feed.testScheduler
import com.paulchibamba.margin.domain.feed.tipOf
import com.paulchibamba.margin.domain.feed.withIntroduced
import com.paulchibamba.margin.domain.memory.CardState
import com.paulchibamba.margin.domain.memory.Rating
import com.paulchibamba.margin.domain.model.Format
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PostActionHandlerTest {

    private val handler = PostActionHandler(testScheduler)
    private val library = libraryWith(readNotes = readingEverything())
    private val learning = freshState.copy(step = 7).withIntroduced(cia, progress = introducedProgress(due = now))
    private val learned = freshState.copy(step = 7).withIntroduced(cia, progress = learnedProgress())

    private fun FeedState.after(action: PostAction) = handler.apply(this, tipOf(cia), action, now).state

    @Test
    fun `Got it marks the concept as got and promises a test soon`() {
        val outcome = handler.apply(learning, tipOf(cia), PostAction.GOT, now)

        assertEquals(Confidence.GOT, outcome.state.progressOf(cia).confidence)
        assertEquals(Nudge.TEST_COMING_SOON, outcome.nudge)
    }

    @Test
    fun `Got it stops further explanations of the concept`() {
        val angles = AngleProvider().candidates(library, learning.after(PostAction.GOT), now)

        assertTrue(angles.none { it.post.conceptId == cia.id })
    }

    @Test
    fun `Got it earns the concept's reviews a prove-it bonus`() {
        val score = scoreOf(Candidate(mcqOf(cia), CandidateSource.REVIEW), learning.after(PostAction.GOT))

        assertEquals(0.4, score.parts[ScorePart.PROVE_IT])
    }

    @Test
    fun `Lost marks the concept lost at the current step and promises another angle`() {
        val outcome = handler.apply(learning, tipOf(cia), PostAction.LOST, now)

        val progress = outcome.state.progressOf(cia)
        assertEquals(Confidence.LOST, progress.confidence)
        assertEquals(7, progress.lostAtStep)
        assertEquals(Nudge.ANOTHER_ANGLE_COMING, outcome.nudge)
    }

    @Test
    fun `Lost earns the concept's angles a reteach bonus`() {
        val score = scoreOf(Candidate(mythOf(cia), CandidateSource.ANGLE), learning.after(PostAction.LOST))

        assertEquals(0.8, score.parts[ScorePart.RETEACH])
    }

    @Test
    fun `Lost holds the concept's reviews until a teach post re-teaches it`() {
        val lost = learning.after(PostAction.LOST)
        val retaught = lost.withProgress(cia.id, lost.progressOf(cia).copy(retaughtAtStep = 8))

        assertTrue(ReviewProvider().candidates(library, lost, now).none { it.post.conceptId == cia.id })
        assertTrue(ReviewProvider().candidates(library, retaught, now).any { it.post.conceptId == cia.id })
    }

    @Test
    fun `Lost on a learned concept counts a lapse with one Again grade`() {
        val outcome = handler.apply(learned, tipOf(cia), PostAction.LOST, now)

        val progress = outcome.state.progressOf(cia)
        assertEquals(CardState.RELEARNING, progress.card?.state)
        assertEquals(1, progress.card?.lapses)
        assertTrue(progress.isLostGraded)
        assertEquals(Rating.AGAIN, outcome.reviewLogEntry?.rating)
        assertEquals(CardState.REVIEW, outcome.reviewLogEntry?.stateBefore)
    }

    @Test
    fun `Lost twice on a learned concept counts one lapse only`() {
        val once = handler.apply(learned, tipOf(cia), PostAction.LOST, now)
        val twice = handler.apply(once.state, mythOf(cia), PostAction.LOST, now.plusSeconds(60))

        assertEquals(1, twice.state.progressOf(cia).card?.lapses)
        assertEquals(once.state.progressOf(cia).card, twice.state.progressOf(cia).card)
        assertNull(twice.reviewLogEntry)
    }

    @Test
    fun `Lost on a concept that was never tested does not grade its card`() {
        val outcome = handler.apply(learning, tipOf(cia), PostAction.LOST, now)

        assertEquals(learning.progressOf(cia).card, outcome.state.progressOf(cia).card)
        assertTrue(!outcome.state.progressOf(cia).isLostGraded)
        assertNull(outcome.reviewLogEntry)
    }

    @Test
    fun `Lost does not touch format affinity`() {
        assertEquals(learning.affinity, learning.after(PostAction.LOST).affinity)
    }

    @Test
    fun `Read leaves the feed state alone and has no nudge`() {
        val outcome = handler.apply(learning, tipOf(cia), PostAction.READ, now)

        assertEquals(learning, outcome.state)
        assertNull(outcome.nudge)
    }

    @Test
    fun `Save keeps the post`() {
        assertEquals(setOf(tipOf(cia).id), learning.after(PostAction.SAVE).savedPosts)
    }

    @Test
    fun `Less halves the format's affinity`() {
        val state = learning.copy(affinity = learning.affinity.afterEngagement(Format.TIP, 1.0))

        assertEquals(0.625 / 2, state.after(PostAction.LESS).affinity.valueOf(Format.TIP), TOLERANCE)
    }

    @Test
    fun `every action is logged with its time, step, post and concept`() {
        PostAction.entries.forEach { action ->
            val entry = handler.apply(learning, tipOf(cia), action, now).logEntry

            assertEquals(ActionLogEntry(now, 7, tipOf(cia).id, cia.id, action), entry)
        }
    }

    private fun scoreOf(candidate: Candidate, state: FeedState) =
        CandidateScorer(FeedConfig(), RecallEstimate(testScheduler, FeedConfig()), FixedRandom())
            .scoreAll(listOf(candidate), library, state, now)
            .single()
            .score

    private companion object {
        const val TOLERANCE = 1e-9
    }
}
