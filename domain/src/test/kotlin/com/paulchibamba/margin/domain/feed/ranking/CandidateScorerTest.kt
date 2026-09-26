package com.paulchibamba.margin.domain.feed.ranking

import com.paulchibamba.margin.domain.feed.*
import com.paulchibamba.margin.domain.memory.FsrsParameters
import com.paulchibamba.margin.domain.memory.FsrsScheduler
import com.paulchibamba.margin.domain.memory.NoFuzz
import com.paulchibamba.margin.domain.memory.Rating
import java.time.Duration
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class CandidateScorerTest {

    private val config = FeedConfig()
    private val scheduler = FsrsScheduler(FsrsParameters.Default, NoFuzz)

    private fun scorer(random: Random = FixedRandom(double = 0.0)) =
        CandidateScorer(config, RecallEstimate(scheduler, config), random)

    private fun scoreOf(candidate: Candidate, state: FeedState = freshState, random: Random = FixedRandom()) =
        scorer(random).scoreAll(listOf(candidate), libraryWith(), state, now).single().score

    @Test
    fun `every candidate scores its source, format affinity and novelty`() {
        val score = scoreOf(Candidate(tipOf(cia), CandidateSource.RESURFACE))

        assertEquals(0.3, score.parts.getValue(ScorePart.SOURCE))
        assertEquals(0.8 * 0.5, score.parts.getValue(ScorePart.FORMAT), TOLERANCE)
        assertEquals(0.4, score.parts.getValue(ScorePart.NOVELTY))
        assertEquals(0.3 + 0.4 + 0.4, score.total, TOLERANCE)
    }

    @Test
    fun `a format seen in the last four posts earns no novelty`() {
        val state = freshState.afterShowing(tipOf(sameOrigin), mythOf(sameOrigin))

        val score = scoreOf(Candidate(tipOf(cia), CandidateSource.RESURFACE), state)

        assertEquals(0.0, score.parts.getValue(ScorePart.NOVELTY))
    }

    @Test
    fun `jitter adds up to five hundredths to break ties`() {
        val score = scoreOf(Candidate(tipOf(cia), CandidateSource.RESURFACE), random = FixedRandom(double = 0.5))

        assertEquals(0.025, score.parts.getValue(ScorePart.JITTER), TOLERANCE)
    }

    @Test
    fun `a review of a never-tested concept is mildly urgent`() {
        val state = freshState.withIntroduced(cia, progress = introducedProgress(due = now))

        val score = scoreOf(Candidate(mcqOf(cia), CandidateSource.REVIEW), state)

        assertEquals(3 * 0.05, score.parts.getValue(ScorePart.URGENCY), TOLERANCE)
    }

    @Test
    fun `urgency grows as recall fades, up to a cap of one half`() {
        val ninetyDaysAgo = now.minus(Duration.ofDays(90))
        val learned = scheduler.next(scheduler.createEmptyCard(ninetyDaysAgo), ninetyDaysAgo, Rating.GOOD)
        val state = freshState.withIntroduced(cia, progress = introducedProgress().copy(card = learned))

        val score = scoreOf(Candidate(mcqOf(cia), CandidateSource.REVIEW), state)

        assertEquals(0.5, score.parts.getValue(ScorePart.URGENCY))
    }

    @Test
    fun `explaining a lost concept again earns the reteach bonus`() {
        val lost = freshState.withIntroduced(cia, progress = introducedProgress().copy(confidence = Confidence.LOST))

        val stillLearning = freshState.withIntroduced(cia)
        val explanation = Candidate(mythOf(cia), CandidateSource.ANGLE)

        assertEquals(0.8, scoreOf(explanation, lost).parts[ScorePart.RETEACH])
        assertNull(scoreOf(explanation, stillLearning).parts[ScorePart.RETEACH])
    }

    @Test
    fun `testing a concept marked got it earns the prove-it bonus`() {
        val gotProgress = introducedProgress(due = now).copy(confidence = Confidence.GOT)
        val got = freshState.withIntroduced(cia, progress = gotProgress)

        assertEquals(0.4, scoreOf(Candidate(mcqOf(cia), CandidateSource.REVIEW), got).parts[ScorePart.PROVE_IT])
    }

    @Test
    fun `a book behind its target share is boosted, capped at six tenths`() {
        val score = scoreOf(Candidate(tipOf(cia), CandidateSource.NEW))

        assertEquals(0.6, score.parts.getValue(ScorePart.PRIORITY))
    }

    @Test
    fun `a book ahead of its target share is held back, capped at minus six tenths`() {
        val state = freshState.withIntroduced(cia, leastPrivilege, defenceInDepth)

        val score = scoreOf(Candidate(tipOf(readingOnlyIntro), CandidateSource.NEW), state)

        assertEquals(-0.6, score.parts.getValue(ScorePart.PRIORITY), TOLERANCE)
    }

    @Test
    fun `a book slightly behind its target share gets a proportional boost`() {
        val state = freshState.withIntroduced(cia, leastPrivilege, sameOrigin)

        val score = scoreOf(Candidate(tipOf(defenceInDepth), CandidateSource.NEW), state)

        assertEquals(3 * (0.75 - 2.0 / 3), score.parts.getValue(ScorePart.PRIORITY), TOLERANCE)
    }

    @Test
    fun `a book that has waited longer for a new concept is nudged forward`() {
        val state = freshState.copy(step = 10, bookLastNewStep = mapOf(grokking.slug to 4))

        val score = scoreOf(Candidate(tipOf(sameOrigin), CandidateSource.NEW), state)

        assertEquals(0.05 * 6, score.parts.getValue(ScorePart.BOOK_WAIT), TOLERANCE)
    }

    @Test
    fun `the book wait bonus is capped at six tenths`() {
        val score = scoreOf(Candidate(tipOf(sameOrigin), CandidateSource.NEW), freshState.copy(step = 40))

        assertEquals(0.6, score.parts.getValue(ScorePart.BOOK_WAIT), TOLERANCE)
    }

    private companion object {
        const val TOLERANCE = 1e-9
    }
}
