package com.paulchibamba.margin.domain.progress

fun interface RewardRule {
    fun seedsFrom(facts: ProgressFacts, history: RewardHistory): List<RewardSeed>
}
