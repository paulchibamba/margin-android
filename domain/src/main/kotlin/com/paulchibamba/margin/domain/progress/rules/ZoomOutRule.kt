package com.paulchibamba.margin.domain.progress.rules

import com.paulchibamba.margin.domain.progress.ChapterProgress
import com.paulchibamba.margin.domain.progress.ConceptHotspot
import com.paulchibamba.margin.domain.progress.FactKey
import com.paulchibamba.margin.domain.progress.ProgressFacts
import com.paulchibamba.margin.domain.progress.RewardHistory
import com.paulchibamba.margin.domain.progress.RewardKind
import com.paulchibamba.margin.domain.progress.RewardRule
import com.paulchibamba.margin.domain.progress.RewardSeed

class ZoomOutRule : RewardRule {

    override fun seedsFrom(facts: ProgressFacts, history: RewardHistory): List<RewardSeed> =
        facts.chapters.flatMap { chapter ->
            listOfNotNull(thresholdSeedOf(chapter, history)) + hotspotSeedsOf(chapter, facts, history)
        }

    private fun thresholdSeedOf(chapter: ChapterProgress, history: RewardHistory): RewardSeed? {
        val threshold = THRESHOLDS.lastOrNull { percent -> chapter.introduced.size * 100 >= percent * chapter.total }
        val label = threshold?.let { percent -> "$percent%" } ?: return null
        val isDone = history.hasAny(RewardKind.ZoomOut) { past ->
            ChapterFacts.isAbout(past, chapter) && past.facts[FactKey.THRESHOLD] == label
        }
        return if (isDone) null else seedOf(chapter, FactKey.THRESHOLD to label)
    }

    private fun hotspotSeedsOf(chapter: ChapterProgress, facts: ProgressFacts, history: RewardHistory) =
        facts.attention.hotspots
            .filter { hotspot -> chapter.contains(hotspot.concept) }
            .filterNot { hotspot -> isDone(hotspot, chapter, history) }
            .map { hotspot -> seedOf(chapter, FactKey.REREAD_CONCEPT to hotspot.concept.title) }

    private fun isDone(hotspot: ConceptHotspot, chapter: ChapterProgress, history: RewardHistory): Boolean =
        history.hasAny(RewardKind.ZoomOut) { past ->
            ChapterFacts.isAbout(past, chapter) && past.facts[FactKey.REREAD_CONCEPT] == hotspot.concept.title
        }

    private fun seedOf(chapter: ChapterProgress, reason: Pair<String, String>) = RewardSeed(
        kind = RewardKind.ZoomOut,
        conceptIds = chapter.introduced.map { concept -> concept.id },
        facts = ChapterFacts.of(chapter) + reason,
    )

    private companion object {
        val THRESHOLDS = listOf(50, 100)
    }
}
