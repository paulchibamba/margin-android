package com.paulchibamba.margin.domain.progress

import com.paulchibamba.margin.domain.model.ConceptId
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RewardPlannerTest {
    private val rewardKinds = RewardKind.all.filter(RewardKind::isReward)

    private fun seedOf(kind: RewardKind, index: Int = 0) =
        RewardSeed(kind, listOf(ConceptId("concept-$index")), facts = mapOf("index" to index.toString()))

    private fun seedsOfEveryKind(perKind: Int = 3) =
        RewardKind.all.flatMap { kind -> (0 until perKind).map { index -> seedOf(kind, index) } }

    private fun plan(seeds: List<RewardSeed>, history: RewardHistory = RewardHistory.Empty, seed: Int = 1) =
        RewardPlanner(Random(seed)).plan(seeds, history, NOW)

    @Test
    fun `a bake plans at most five rewards and never two of one kind`() {
        repeat(50) { randomSeed ->
            val rewards = plan(seedsOfEveryKind(), seed = randomSeed).rewards

            assertEquals(5, rewards.size)
            assertEquals(rewards.size, rewards.map(RewardSeed::kind).distinct().size)
        }
    }

    @Test
    fun `kinds not shown in the last three days come first`() {
        val history = RewardHistory(
            listOf(
                pastReward(RewardKind.ZoomOut, shownAt = daysAgo(1)),
                pastReward(RewardKind.Quote, shownAt = daysAgo(2)),
            ),
        )

        repeat(20) { randomSeed ->
            val kinds = plan(seedsOfEveryKind(), history, randomSeed).rewards.map(RewardSeed::kind).toSet()

            assertEquals(rewardKinds.toSet() - RewardKind.ZoomOut - RewardKind.Quote, kinds)
        }
    }

    @Test
    fun `now you can is planned at most once every two days`() {
        val seeds = listOf(seedOf(RewardKind.NowYouCan), seedOf(RewardKind.Comeback))
        val recent = RewardHistory(listOf(pastReward(RewardKind.NowYouCan, createdAt = daysAgo(1))))
        val older = RewardHistory(listOf(pastReward(RewardKind.NowYouCan, createdAt = daysAgo(3))))

        assertEquals(listOf(RewardKind.Comeback), plan(seeds, recent).rewards.map(RewardSeed::kind))
        assertTrue(RewardKind.NowYouCan in plan(seeds, older).rewards.map(RewardSeed::kind))
    }

    @Test
    fun `re-explains are planned apart from rewards, one per concept, outside the limits`() {
        val reExplains = (0 until 7).map { index -> seedOf(RewardKind.ReExplain, index) }

        val plan = plan(seedsOfEveryKind(perKind = 1) + reExplains)

        assertTrue(plan.rewards.none { it.kind == RewardKind.ReExplain })
        assertEquals(7, plan.reExplains.size)
    }

    @Test
    fun `the same random seed gives the same plan`() {
        assertEquals(plan(seedsOfEveryKind(), seed = 9), plan(seedsOfEveryKind(), seed = 9))
    }
}
