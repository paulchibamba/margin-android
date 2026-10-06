package com.paulchibamba.margin.domain.drop

import com.paulchibamba.margin.domain.feed.ReviewLogEntry
import com.paulchibamba.margin.domain.feed.cia
import com.paulchibamba.margin.domain.feed.freshState
import com.paulchibamba.margin.domain.feed.introducedProgress
import com.paulchibamba.margin.domain.feed.leastPrivilege
import com.paulchibamba.margin.domain.feed.now
import com.paulchibamba.margin.domain.feed.withIntroduced
import com.paulchibamba.margin.domain.memory.CardState
import com.paulchibamba.margin.domain.memory.MemoryCard
import com.paulchibamba.margin.domain.memory.Rating
import com.paulchibamba.margin.domain.model.Concept
import com.paulchibamba.margin.domain.model.PostId
import kotlin.test.Test
import kotlin.test.assertEquals

class DropEvidenceFinderTest {
    private val remembered = introducedProgress().copy(
        card = MemoryCard.new(now).copy(state = CardState.REVIEW, scheduledDays = 24, stability = 26.0),
    )

    private fun reviewOf(concept: Concept, rating: Rating, stabilityBefore: Double = 9.0) =
        ReviewLogEntry(now, concept.id, PostId("quiz"), rating, CardState.REVIEW, stabilityBefore, 26.0, null)

    @Test
    fun `ideas that moved to remembered today come first`() {
        val state = freshState.withIntroduced(cia, progress = remembered).withIntroduced(leastPrivilege)
        val reviews = listOf(reviewOf(cia, Rating.GOOD), reviewOf(leastPrivilege, Rating.GOOD))

        assertEquals(DropEvidence.Remembered(1), DropEvidenceFinder.strongest(TodaysLearning(state, reviews, 2, 9)))
    }

    @Test
    fun `an idea that was already remembered did not move`() {
        val state = freshState.withIntroduced(cia, progress = remembered)
        val reviews = listOf(reviewOf(cia, Rating.GOOD, stabilityBefore = 30.0))

        assertEquals(DropEvidence.Passed(1), DropEvidenceFinder.strongest(TodaysLearning(state, reviews, 0, 4)))
    }

    @Test
    fun `without passed reviews it counts new ideas, then posts`() {
        val failed = listOf(reviewOf(cia, Rating.AGAIN))

        assertEquals(DropEvidence.Introduced(2), DropEvidenceFinder.strongest(TodaysLearning(freshState, failed, 2, 5)))
        assertEquals(DropEvidence.PostsSeen(5), DropEvidenceFinder.strongest(TodaysLearning(freshState, failed, 0, 5)))
    }
}
