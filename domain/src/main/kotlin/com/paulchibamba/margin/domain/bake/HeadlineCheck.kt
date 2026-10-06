package com.paulchibamba.margin.domain.bake

import com.paulchibamba.margin.domain.progress.BannedPatterns
import com.paulchibamba.margin.domain.progress.FactsOnlyCheck
import com.paulchibamba.margin.domain.progress.RewardDraft
import com.paulchibamba.margin.domain.progress.RewardKind
import com.paulchibamba.margin.domain.progress.RewardSeed

object HeadlineCheck {
    const val MAX_LENGTH = 50

    fun validOrNull(headline: String, seeds: List<RewardSeed>): String? {
        val trimmed = headline.trim()
        return trimmed.takeIf { isValid(trimmed, factsOf(seeds)) }
    }

    private fun isValid(headline: String, facts: Map<String, String>): Boolean =
        headline.isNotEmpty() && headline.length <= MAX_LENGTH && '<' !in headline &&
            !BannedPatterns.isBanned(RewardDraft(headline, body = ""), RewardKind.Milestone) &&
            FactsOnlyCheck.hasOnlyFactNumbers(headline, facts)

    private fun factsOf(seeds: List<RewardSeed>): Map<String, String> =
        seeds.flatMap { seed -> seed.facts.values }.withIndex().associate { (index, value) -> "$index" to value }
}
