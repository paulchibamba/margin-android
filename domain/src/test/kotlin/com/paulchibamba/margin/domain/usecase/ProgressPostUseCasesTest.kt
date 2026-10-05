package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.actions.ActionLogEntry
import com.paulchibamba.margin.domain.actions.PostAction
import com.paulchibamba.margin.domain.feed.CandidateSource
import com.paulchibamba.margin.domain.feed.FeedResult
import com.paulchibamba.margin.domain.feed.FeedState
import com.paulchibamba.margin.domain.feed.ReviewLogEntry
import com.paulchibamba.margin.domain.feed.cia
import com.paulchibamba.margin.domain.feed.generatedPostOf
import com.paulchibamba.margin.domain.feed.introducedProgress
import com.paulchibamba.margin.domain.feed.leastPrivilege
import com.paulchibamba.margin.domain.feed.mcqOf
import com.paulchibamba.margin.domain.feed.memeOf
import com.paulchibamba.margin.domain.feed.withIntroduced
import com.paulchibamba.margin.domain.feed.withSeen
import com.paulchibamba.margin.domain.memory.CardState
import com.paulchibamba.margin.domain.memory.Rating
import com.paulchibamba.margin.domain.model.AffinityKey
import com.paulchibamba.margin.domain.model.Format
import com.paulchibamba.margin.domain.progress.RewardKind
import com.paulchibamba.margin.domain.signals.FormatAffinity
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ProgressPostUseCasesTest {
    private val fixture = UseCaseFixture()
    private val comeback = generatedPostOf(RewardKind.Comeback, cia)

    @Test
    fun `a progress post picked for the reward slot is marked shown`() = runTest {
        fixture.generatedPosts.posts += comeback
        fixture.progress.feedState.value = FeedState(rewardAtStep = 0, lastPreviewAtStep = 0)
            .withIntroduced(cia, leastPrivilege)
            .withSeen(memeOf(cia), memeOf(leastPrivilege))

        val items = (1..5).map { assertIs<FeedResult.Next>(fixture.getNextPost()).item }

        assertEquals(comeback.id, items.first { it.source == CandidateSource.REWARD }.post.id)
        assertNotNull(fixture.generatedPosts.posts.single().shownAt)
    }

    @Test
    fun `Less on a progress post marks it and halves that kind's affinity, not the format's`() = runTest {
        fixture.progress.feedState.value = FeedState(rewardAtStep = 9).withIntroduced(cia)
        fixture.generatedPosts.posts += comeback

        fixture.applyPostAction(comeback.toPost(cia.bookSlug)!!, PostAction.LESS)

        val affinity = fixture.progress.feedState.value!!.affinity
        assertTrue(fixture.generatedPosts.posts.single().isLessPressed)
        assertEquals(0.25, affinity.valueOf(AffinityKey.OfRewardKind(RewardKind.Comeback.key)))
        assertEquals(FormatAffinity.NEUTRAL, affinity.valueOf(Format.PROGRESS))
    }

    @Test
    fun `a review graded Again makes the concept struggle until a review passes`() = runTest {
        val due = introducedProgress(due = fixture.clock.instant.minusSeconds(60))
        fixture.progress.feedState.value = FeedState(rewardAtStep = 99).withIntroduced(cia, progress = due)
        fixture.progress.reviews.value = listOf(reviewOf(Rating.AGAIN, secondsAgo = 600))

        assertTrue(fixture.libraryLoader.load().isStruggling(cia))

        fixture.progress.reviews.value += reviewOf(Rating.GOOD, secondsAgo = 60)
        assertFalse(fixture.libraryLoader.load().isStruggling(cia))
    }

    @Test
    fun `a Lost alone is left to the existing re-teach rule`() = runTest {
        val lost = ActionLogEntry(fixture.clock.instant.minusSeconds(60), 3, mcqOf(cia).id, cia.id, PostAction.LOST)
        fixture.progress.actions.value = listOf(lost)

        assertFalse(fixture.libraryLoader.load().isStruggling(cia))
    }

    @Test
    fun `the library holds fresh progress posts only`() = runTest {
        fixture.generatedPosts.posts += comeback
        fixture.generatedPosts.posts += generatedPostOf(RewardKind.Quote, cia).copy(isLessPressed = true)

        assertEquals(listOf(comeback.id), fixture.libraryLoader.load().rewardPosts.map { it.id })
    }

    private fun reviewOf(rating: Rating, secondsAgo: Long) = ReviewLogEntry(
        fixture.clock.instant.minusSeconds(secondsAgo), cia.id, mcqOf(cia).id, rating, CardState.REVIEW, 2.0, 0.5, null,
    )
}
