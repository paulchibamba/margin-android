package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.bake.BakeReport
import com.paulchibamba.margin.domain.bake.BakeSeed
import com.paulchibamba.margin.domain.bake.BakeSeeds
import com.paulchibamba.margin.domain.bake.BakeSelection
import com.paulchibamba.margin.domain.bake.BakeState
import com.paulchibamba.margin.domain.bake.BakeTrigger
import com.paulchibamba.margin.domain.bake.ProgressPostWriter
import com.paulchibamba.margin.domain.progress.GeneratedPost
import com.paulchibamba.margin.domain.progress.ProgressFacts
import com.paulchibamba.margin.domain.progress.RewardEligibility
import com.paulchibamba.margin.domain.progress.RewardHistory
import com.paulchibamba.margin.domain.progress.RewardPlanner
import com.paulchibamba.margin.domain.progress.RewardSeed
import com.paulchibamba.margin.domain.repository.BakeStateStore
import com.paulchibamba.margin.domain.repository.Clock
import com.paulchibamba.margin.domain.repository.ContentRepository
import com.paulchibamba.margin.domain.repository.GeneratedPostRepository
import java.time.Instant
import javax.inject.Inject
import kotlin.random.Random

class BakeProgressPosts @Inject constructor(
    private val buildFacts: BuildProgressFacts,
    private val generatedPosts: GeneratedPostRepository,
    private val content: ContentRepository,
    private val bakeState: BakeStateStore,
    private val writer: ProgressPostWriter,
    private val clock: Clock,
    private val random: Random,
) {
    suspend operator fun invoke(trigger: BakeTrigger): BakeReport {
        val now = clock.now()
        val baked = generatedPosts.all()
        val state = bakeState.load()
        val history = RewardHistory(baked.map(GeneratedPost::toPastReward), state.lastBakeAt)
        val facts = buildFacts(history)
        val seeds = BakeSelection.unbaked(RewardEligibility().seedsFrom(facts, history), hashesOf(baked))
        val plan = RewardPlanner(random).plan(seeds, history, now)
        val sources = BakeSeeds.load(content, plan.rewards + plan.reExplains)
        val reExplains = plan.reExplains.count { seed -> bakeReExplain(sources.of(seed), now) }
        val rewardSeeds = seeds.filter { seed -> seed.kind.isReward }
        val isNothingNew = !trigger.bakesRewards || !hasNews(trigger, facts, rewardSeeds, state)
        if (isNothingNew) return BakeReport(rewards = 0, reExplains, isNothingNew = trigger.bakesRewards)
        val rewards = bakeRewards(plan.rewards.map(sources::of), rewardSeeds, state, now)
        return BakeReport(rewards, reExplains, isNothingNew = false)
    }

    private fun hasNews(trigger: BakeTrigger, facts: ProgressFacts, seeds: List<RewardSeed>, state: BakeState) =
        BakeSelection.hasNewSeeds(seeds, state) && (!trigger.needsNewFacts || hasNewFacts(facts))

    private fun hasNewFacts(facts: ProgressFacts): Boolean =
        facts.introducedToday.isNotEmpty() || facts.comebacks.isNotEmpty() || facts.newlyRemembered.isNotEmpty()

    private suspend fun bakeReExplain(seed: BakeSeed, now: Instant): Boolean {
        val post = writer.writeReExplain(seed, now) ?: return false
        return generatedPosts.insert(listOf(post)) > 0
    }

    private suspend fun bakeRewards(
        planned: List<BakeSeed>,
        eligible: List<RewardSeed>,
        state: BakeState,
        now: Instant,
    ): Int {
        val written = writer.writeRewards(planned, now)
        val inserted = generatedPosts.insert(written.posts)
        val seen = state.seenSeeds + eligible.map(RewardSeed::factsHash)
        bakeState.save(BakeState(lastBakeAt = now, seenSeeds = seen, nextDropHeadline = written.headline))
        return inserted
    }

    private fun hashesOf(posts: List<GeneratedPost>): Set<String> = posts.map(GeneratedPost::factsHash).toSet()
}
