package com.paulchibamba.margin.domain.progress

import com.paulchibamba.margin.domain.feed.ReviewLogEntry
import com.paulchibamba.margin.domain.memory.Rating
import com.paulchibamba.margin.domain.model.ConceptId
import com.paulchibamba.margin.domain.model.NoteId
import java.time.Instant

class RewardExpiry(
    private val readNotes: Set<NoteId>,
    private val reviews: List<ReviewLogEntry>,
    private val existingConcepts: Set<ConceptId>,
) {
    fun isFresh(post: GeneratedPost, now: Instant): Boolean =
        !post.isShown && !post.isLessPressed && !isExpired(post, now) && !isOrphan(post)

    fun isExpired(post: GeneratedPost, now: Instant): Boolean = when (post.kind) {
        RewardKind.ComingUp -> post.noteId in readNotes
        RewardKind.ReExplain -> hasPassedSince(post.conceptIds.firstOrNull(), post.createdAt)
        else -> post.expiresAt?.let { expiresAt -> !now.isBefore(expiresAt) } ?: false
    }

    fun isOrphan(post: GeneratedPost): Boolean = post.conceptIds.any { concept -> concept !in existingConcepts }

    private fun hasPassedSince(concept: ConceptId?, since: Instant): Boolean = reviews.any { review ->
        review.conceptId == concept && review.rating != Rating.AGAIN && review.at.isAfter(since)
    }
}
