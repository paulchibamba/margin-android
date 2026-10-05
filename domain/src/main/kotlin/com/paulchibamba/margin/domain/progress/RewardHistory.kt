package com.paulchibamba.margin.domain.progress

import com.paulchibamba.margin.domain.model.ConceptId
import com.paulchibamba.margin.domain.model.NoteId
import java.time.Instant

data class RewardHistory(val rewards: List<PastReward> = emptyList(), val lastBakeAt: Instant? = null) {

    fun of(kind: RewardKind): List<PastReward> = rewards.filter { reward -> reward.kind == kind }

    fun lastShownAt(kind: RewardKind): Instant? = of(kind).mapNotNull(PastReward::shownAt).maxOrNull()

    fun lastMadeAt(kind: RewardKind): Instant? =
        of(kind).maxOfOrNull { reward -> reward.shownAt ?: reward.createdAt }

    fun quotedNotes(): Set<NoteId> = of(RewardKind.Quote).mapNotNull(PastReward::noteId).toSet()

    fun hasAny(kind: RewardKind, matching: (PastReward) -> Boolean): Boolean = of(kind).any(matching)

    fun of(kind: RewardKind, concept: ConceptId): List<PastReward> = of(kind).filter { concept in it.conceptIds }

    companion object {
        val Empty = RewardHistory()
    }
}
