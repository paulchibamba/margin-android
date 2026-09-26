package com.paulchibamba.margin.domain.feed

import com.paulchibamba.margin.domain.memory.MemoryCard
import java.time.Instant

data class ConceptProgress(
    val introducedAtStep: Int? = null,
    val card: MemoryCard? = null,
    val confidence: Confidence? = null,
    val isLostGraded: Boolean = false,
    val lostAtStep: Int? = null,
    val retaughtAtStep: Int? = null,
    val lastShownStep: Int? = null,
) {
    val isIntroduced: Boolean
        get() = introducedAtStep != null

    val isAwaitingReteach: Boolean
        get() = confidence == Confidence.LOST && (retaughtAtStep ?: -1) <= (lostAtStep ?: 0)

    fun isDueForReview(now: Instant): Boolean = card?.due?.isAfter(now) == false

    companion object {
        val Untouched = ConceptProgress()
    }
}
