package com.paulchibamba.margin.domain.feed

import com.paulchibamba.margin.domain.model.BookSlug
import com.paulchibamba.margin.domain.model.Concept
import com.paulchibamba.margin.domain.model.ConceptId
import com.paulchibamba.margin.domain.model.PostId
import com.paulchibamba.margin.domain.signals.FormatAffinity

data class FeedState(
    val delightAtStep: Int,
    val step: Int = 0,
    val conceptProgress: Map<ConceptId, ConceptProgress> = emptyMap(),
    val seenPosts: Map<PostId, Int> = emptyMap(),
    val history: List<FeedHistoryEntry> = emptyList(),
    val bookLastNewStep: Map<BookSlug, Int> = emptyMap(),
    val lastPreviewAtStep: Int? = null,
    val affinity: FormatAffinity = FormatAffinity(),
) {
    val isDelightDue: Boolean
        get() = step >= delightAtStep

    fun progressOf(concept: ConceptId): ConceptProgress = conceptProgress[concept] ?: ConceptProgress.Untouched

    fun progressOf(concept: Concept): ConceptProgress = progressOf(concept.id)

    fun isIntroduced(concept: Concept): Boolean = progressOf(concept).isIntroduced

    fun hasSeen(post: PostId): Boolean = post in seenPosts
}
