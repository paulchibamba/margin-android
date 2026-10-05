package com.paulchibamba.margin.domain.simulation

import com.paulchibamba.margin.domain.feed.CandidateSource
import com.paulchibamba.margin.domain.feed.FeedItem
import com.paulchibamba.margin.domain.progress.GeneratedPost
import com.paulchibamba.margin.domain.progress.RewardDraft
import com.paulchibamba.margin.domain.progress.RewardKind
import com.paulchibamba.margin.domain.progress.RewardSeed
import org.junit.Assume.assumeTrue
import org.junit.Before
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RewardSlotPropertiesTest {
    private lateinit var pack: TestPack

    @Before
    fun loadPack() {
        val loaded = TestPackLoader.pack
        assumeTrue("No content pack in content/pack: run scripts/sync-content-pack.sh", loaded != null)
        pack = loaded!!
    }

    private fun progressPostsFor(pack: TestPack): List<GeneratedPost> =
        pack.concepts.flatMap { concept ->
            RewardKind.all.filter(RewardKind::isReward).map { kind ->
                val seed = RewardSeed(kind, listOf(concept.id), facts = mapOf("concept" to concept.id.value))
                GeneratedPost.from(seed, RewardDraft(kind.key, "Body"), GeneratedPost.TEMPLATE_WRITER, CREATED)
            }
        }

    private fun sessions(runs: Int = 50): List<List<FeedItem>> = (1..runs).map { seed ->
        FeedSimulation(pack, FeedSimulation.EVERYTHING, seed, generatedPosts = progressPostsFor(pack)).scroll(40)
    }

    private fun rewardStepsOf(session: List<FeedItem>): List<Int> =
        session.withIndex().filter { (_, item) -> item.source == CandidateSource.REWARD }.map { it.index }

    @Test
    fun `over 40 posts rewards never come back to back and come every 6 to 10 posts on average`() {
        val sessions = sessions()

        val backToBack = sessions.sumOf { session ->
            rewardStepsOf(session).zipWithNext().count { (previous, next) -> next == previous + 1 }
        }
        val gaps = sessions.flatMap { session ->
            rewardStepsOf(session).zipWithNext { previous, next -> next - previous }
        }

        assertEquals(0, backToBack)
        assertTrue(gaps.average() in 6.0..10.0, "mean gap ${gaps.average()}")
    }

    @Test
    fun `the same reward kind never comes twice in a row unless the filter had to relax`() {
        val repeats = sessions().sumOf { session ->
            session.filter { it.source == CandidateSource.REWARD && it.post.rewardKind != null }
                .zipWithNext()
                .count { (previous, next) ->
                    previous.post.rewardKind == next.post.rewardKind && "reward variety" in next.appliedFilters
                }
        }

        assertEquals(0, repeats)
    }

    @Test
    fun `progress posts do show up in the reward slot`() {
        val shown = sessions(runs = 10).flatten()
            .count { it.source == CandidateSource.REWARD && it.post.rewardKind != null }

        assertTrue(shown > 0)
    }

    private companion object {
        val CREATED: Instant = Instant.parse("2026-10-01T07:00:00Z")
    }
}
