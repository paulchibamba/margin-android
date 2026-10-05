package com.paulchibamba.margin.domain.progress

import java.time.Duration
import java.time.Instant
import kotlin.random.Random

class RewardPlanner(private val random: Random) {

    fun plan(seeds: List<RewardSeed>, history: RewardHistory, now: Instant): RewardPlan = RewardPlan(
        rewards = rewardsFrom(seeds.filter { seed -> seed.kind.isReward }, history, now),
        reExplains = seeds.filter { seed -> seed.kind == RewardKind.ReExplain }.distinctBy(RewardSeed::conceptIds),
    )

    private fun rewardsFrom(seeds: List<RewardSeed>, history: RewardHistory, now: Instant): List<RewardSeed> {
        val seedsByKind = seeds.groupBy(RewardSeed::kind).filterKeys { kind -> !isRateLimited(kind, history, now) }
        val (fresh, recent) = seedsByKind.keys.shuffled(random).partition { !wasShownRecently(it, history, now) }
        return (fresh + recent).take(MAX_SEEDS).map { kind -> seedsByKind.getValue(kind).random(random) }
    }

    private fun isRateLimited(kind: RewardKind, history: RewardHistory, now: Instant): Boolean =
        kind == RewardKind.NowYouCan && history.lastMadeAt(kind)?.isAfter(now.minus(NOW_YOU_CAN_GAP)) == true

    private fun wasShownRecently(kind: RewardKind, history: RewardHistory, now: Instant): Boolean =
        history.lastShownAt(kind)?.isAfter(now.minus(VARIETY_WINDOW)) == true

    companion object {
        const val MAX_SEEDS = 5
        val NOW_YOU_CAN_GAP: Duration = Duration.ofDays(2)
        val VARIETY_WINDOW: Duration = Duration.ofDays(3)
    }
}
