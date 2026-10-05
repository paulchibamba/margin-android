package com.paulchibamba.margin.domain.progress.rules

import com.paulchibamba.margin.domain.model.Concept
import com.paulchibamba.margin.domain.progress.FactKey
import com.paulchibamba.margin.domain.progress.ProgressFacts
import com.paulchibamba.margin.domain.progress.RewardHistory
import com.paulchibamba.margin.domain.progress.RewardKind
import com.paulchibamba.margin.domain.progress.RewardRule
import com.paulchibamba.margin.domain.progress.RewardSeed

class NowYouCanRule : RewardRule {

    override fun seedsFrom(facts: ProgressFacts, history: RewardHistory): List<RewardSeed> =
        winsOf(facts)
            .distinctBy { (concept, _) -> concept.id }
            .filter { (concept, _) -> history.of(RewardKind.NowYouCan, concept.id).isEmpty() }
            .map { (concept, win) -> seedOf(concept, win) }

    private fun winsOf(facts: ProgressFacts): List<Pair<Concept, String>> =
        facts.newlyRemembered.map { concept -> concept to WIN_REMEMBERED } +
            facts.comebacks.map { comeback -> comeback.concept to WIN_COMEBACK }

    private fun seedOf(concept: Concept, win: String) = RewardSeed(
        kind = RewardKind.NowYouCan,
        conceptIds = listOf(concept.id),
        facts = mapOf(FactKey.CONCEPT to concept.title, FactKey.SUMMARY to concept.summary, FactKey.WIN to win),
    )

    companion object {
        const val WIN_REMEMBERED = "remembered"
        const val WIN_COMEBACK = "comeback"
    }
}
