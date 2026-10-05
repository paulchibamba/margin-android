package com.paulchibamba.margin.domain.progress.rules

import com.paulchibamba.margin.domain.model.Concept
import com.paulchibamba.margin.domain.progress.FactKey
import com.paulchibamba.margin.domain.progress.ProgressFacts
import com.paulchibamba.margin.domain.progress.RewardHistory
import com.paulchibamba.margin.domain.progress.RewardKind
import com.paulchibamba.margin.domain.progress.RewardRule
import com.paulchibamba.margin.domain.progress.RewardSeed
import com.paulchibamba.margin.domain.progress.UpcomingNote

class ComingUpRule : RewardRule {

    override fun seedsFrom(facts: ProgressFacts, history: RewardHistory): List<RewardSeed> =
        facts.upcomingNotes
            .mapNotNull { upcoming -> seedOf(upcoming, facts) }
            .filterNot { seed ->
                history.hasAny(RewardKind.ComingUp) { it.noteId == seed.noteId && it.conceptIds == seed.conceptIds }
            }

    private fun seedOf(upcoming: UpcomingNote, facts: ProgressFacts): RewardSeed? {
        val known = facts.knownConcepts()
        val (concept, knownConcept) = upcoming.concepts
            .filterNot { concept -> concept in facts.introduced }
            .firstNotNullOfOrNull { concept -> relatedPair(concept, known, facts) }
            ?: return null
        return RewardSeed(
            kind = RewardKind.ComingUp,
            conceptIds = listOf(concept.id, knownConcept.id),
            noteId = upcoming.note.id,
            facts = mapOf(
                FactKey.CONCEPT to concept.title,
                FactKey.KNOWN_CONCEPT to knownConcept.title,
                FactKey.NOTES_AWAY to upcoming.notesAway.toString(),
                FactKey.SECTION to upcoming.note.section,
            ),
        )
    }

    private fun relatedPair(concept: Concept, known: List<Concept>, facts: ProgressFacts): Pair<Concept, Concept>? =
        facts.relations.relatedTo(concept, known).firstOrNull()?.let { knownConcept -> concept to knownConcept }
}
