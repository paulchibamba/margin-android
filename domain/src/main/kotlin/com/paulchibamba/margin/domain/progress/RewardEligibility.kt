package com.paulchibamba.margin.domain.progress

import com.paulchibamba.margin.domain.progress.rules.CallbackRule
import com.paulchibamba.margin.domain.progress.rules.ComebackRule
import com.paulchibamba.margin.domain.progress.rules.ComingUpRule
import com.paulchibamba.margin.domain.progress.rules.MilestoneRule
import com.paulchibamba.margin.domain.progress.rules.NowYouCanRule
import com.paulchibamba.margin.domain.progress.rules.QuoteRule
import com.paulchibamba.margin.domain.progress.rules.ReExplainRule
import com.paulchibamba.margin.domain.progress.rules.ZoomOutRule

class RewardEligibility(private val rules: List<RewardRule> = defaultRules()) {

    fun seedsFrom(facts: ProgressFacts, history: RewardHistory): List<RewardSeed> =
        rules.flatMap { rule -> rule.seedsFrom(facts, history) }.distinctBy(RewardSeed::factsHash)

    companion object {
        fun defaultRules(): List<RewardRule> = listOf(
            ZoomOutRule(),
            ComingUpRule(),
            CallbackRule(),
            ComebackRule(),
            QuoteRule(),
            NowYouCanRule(),
            MilestoneRule(),
            ReExplainRule(),
        )
    }
}
