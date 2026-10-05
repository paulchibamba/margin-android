package com.paulchibamba.margin.domain.progress.rules

import com.paulchibamba.margin.domain.model.Concept
import com.paulchibamba.margin.domain.progress.Angle
import com.paulchibamba.margin.domain.progress.FactKey
import com.paulchibamba.margin.domain.progress.ProgressFacts
import com.paulchibamba.margin.domain.progress.RewardHistory
import com.paulchibamba.margin.domain.progress.RewardKind
import com.paulchibamba.margin.domain.progress.RewardRule
import com.paulchibamba.margin.domain.progress.RewardSeed
import com.paulchibamba.margin.domain.progress.Struggle
import kotlin.math.abs

class ReExplainRule : RewardRule {

    override fun seedsFrom(facts: ProgressFacts, history: RewardHistory): List<RewardSeed> =
        facts.struggles
            .filterNot { struggle -> isRateLimited(struggle, facts, history) }
            .map { struggle -> seedOf(struggle, facts, history) }

    private fun isRateLimited(struggle: Struggle, facts: ProgressFacts, history: RewardHistory): Boolean =
        history.of(RewardKind.ReExplain, struggle.concept.id).any { past ->
            facts.dateOf(past.createdAt) == facts.today || !past.createdAt.isBefore(struggle.at)
        }

    private fun seedOf(struggle: Struggle, facts: ProgressFacts, history: RewardHistory): RewardSeed {
        val concept = struggle.concept
        val angles = facts.anglesShown[concept.id].orEmpty() + earlierReExplainsOf(concept, history)
        return RewardSeed(
            kind = RewardKind.ReExplain,
            conceptIds = listOf(concept.id),
            noteId = concept.sourceNoteId,
            facts = buildMap {
                put(FactKey.CONCEPT, concept.title)
                put(FactKey.SUMMARY, concept.summary)
                put(FactKey.TRIGGER, struggle.trigger.key)
                if (angles.isNotEmpty()) put(FactKey.ANGLES_SHOWN, angles.joinToString(ANGLE_SEPARATOR) { it.label })
                anchorFor(concept, facts.remembered)?.let { anchor -> put(FactKey.ANCHOR, anchor.title) }
            },
        )
    }

    private fun earlierReExplainsOf(concept: Concept, history: RewardHistory): List<Angle> =
        history.of(RewardKind.ReExplain, concept.id).map { past -> Angle(RewardKind.ReExplain.key, past.title) }

    private fun anchorFor(concept: Concept, remembered: List<Concept>): Concept? = remembered
        .filter { candidate -> candidate.id != concept.id }
        .withIndex()
        .minWithOrNull(compareBy({ it.value.bookSlug != concept.bookSlug }, { distance(concept, it.value, it.index) }))
        ?.value

    private fun distance(concept: Concept, candidate: Concept, index: Int): Int =
        if (candidate.bookSlug != concept.bookSlug) index
        else abs((candidate.chapter - concept.chapter) * CHAPTER_WEIGHT + candidate.order - concept.order)

    private companion object {
        const val ANGLE_SEPARATOR = " | "
        const val CHAPTER_WEIGHT = 10_000
    }
}
