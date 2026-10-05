package com.paulchibamba.margin.domain.progress.rules

import com.paulchibamba.margin.domain.progress.FactKey
import com.paulchibamba.margin.domain.progress.Introduction
import com.paulchibamba.margin.domain.progress.ProgressFacts
import com.paulchibamba.margin.domain.progress.RewardHistory
import com.paulchibamba.margin.domain.progress.RewardKind
import com.paulchibamba.margin.domain.progress.RewardRule
import com.paulchibamba.margin.domain.progress.RewardSeed
import java.time.temporal.ChronoUnit

class CallbackRule : RewardRule {

    override fun seedsFrom(facts: ProgressFacts, history: RewardHistory): List<RewardSeed> =
        facts.introducedToday
            .mapNotNull { today -> seedOf(today, facts) }
            .filterNot { seed -> history.hasAny(RewardKind.Callback) { it.conceptIds == seed.conceptIds } }

    private fun seedOf(today: Introduction, facts: ProgressFacts): RewardSeed? {
        val older = facts.introducedAtLeastDaysAgo(MIN_DAYS_AGO)
            .filter { earlier -> facts.relations.areRelated(today.concept, earlier.concept) }
            .minByOrNull(Introduction::on)
            ?: return null
        return RewardSeed(
            kind = RewardKind.Callback,
            conceptIds = listOf(today.concept.id, older.concept.id),
            facts = mapOf(
                FactKey.CONCEPT to today.concept.title,
                FactKey.OLDER_CONCEPT to older.concept.title,
                FactKey.DAYS_AGO to ChronoUnit.DAYS.between(older.on, today.on).toString(),
            ),
        )
    }

    private companion object {
        const val MIN_DAYS_AGO = 7L
    }
}
