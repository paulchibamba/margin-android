package com.paulchibamba.margin.domain.progress.rules

import com.paulchibamba.margin.domain.progress.Comeback
import com.paulchibamba.margin.domain.progress.FactFormat
import com.paulchibamba.margin.domain.progress.FactKey
import com.paulchibamba.margin.domain.progress.ProgressFacts
import com.paulchibamba.margin.domain.progress.RewardHistory
import com.paulchibamba.margin.domain.progress.RewardKind
import com.paulchibamba.margin.domain.progress.RewardRule
import com.paulchibamba.margin.domain.progress.RewardSeed

class ComebackRule : RewardRule {

    override fun seedsFrom(facts: ProgressFacts, history: RewardHistory): List<RewardSeed> =
        facts.comebacks.filterNot { comeback -> isDone(comeback, history) }.map(::seedOf)

    private fun isDone(comeback: Comeback, history: RewardHistory): Boolean =
        history.of(RewardKind.Comeback, comeback.concept.id).any { past ->
            past.facts[FactKey.DATE] == FactFormat.date(comeback.firstFailOn)
        }

    private fun seedOf(comeback: Comeback) = RewardSeed(
        kind = RewardKind.Comeback,
        conceptIds = listOf(comeback.concept.id),
        facts = mapOf(
            FactKey.CONCEPT to comeback.concept.title,
            FactKey.DATE to FactFormat.date(comeback.firstFailOn),
            FactKey.FAIL_COUNT to comeback.failCount.toString(),
            FactKey.PASS_COUNT to comeback.passCount.toString(),
        ),
    )
}
