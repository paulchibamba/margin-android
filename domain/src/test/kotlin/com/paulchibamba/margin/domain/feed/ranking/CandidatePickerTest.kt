package com.paulchibamba.margin.domain.feed.ranking

import com.paulchibamba.margin.domain.feed.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CandidatePickerTest {

    private val ranked = listOf(tipOf(cia), mythOf(cia), tipOf(sameOrigin)).mapIndexed { index, post ->
        ScoredCandidate(Candidate(post, CandidateSource.NEW), ScoreBreakdown(mapOf(ScorePart.SOURCE to 3.0 - index)))
    }

    @Test
    fun `usually the best candidate is taken`() {
        val pick = CandidatePicker(epsilon = 0.15, random = FixedRandom(double = 0.9)).pick(ranked)

        assertEquals(ranked.first(), pick.chosen)
        assertEquals(1, pick.rank)
        assertFalse(pick.wasExploration)
    }

    @Test
    fun `sometimes a lower-ranked candidate is explored instead`() {
        val pick = CandidatePicker(epsilon = 0.15, random = FixedRandom(double = 0.1, offset = 1)).pick(ranked)

        assertEquals(ranked[2], pick.chosen)
        assertEquals(3, pick.rank)
        assertTrue(pick.wasExploration)
    }

    @Test
    fun `a lone candidate is never an exploration`() {
        val pick = CandidatePicker(epsilon = 1.0, random = FixedRandom(double = 0.0)).pick(ranked.take(1))

        assertFalse(pick.wasExploration)
    }
}
