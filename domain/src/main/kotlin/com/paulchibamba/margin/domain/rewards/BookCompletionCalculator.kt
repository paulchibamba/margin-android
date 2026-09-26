package com.paulchibamba.margin.domain.rewards

import com.paulchibamba.margin.domain.feed.ConceptProgress
import com.paulchibamba.margin.domain.memory.CardState
import com.paulchibamba.margin.domain.model.BookSlug
import com.paulchibamba.margin.domain.model.Concept
import com.paulchibamba.margin.domain.model.ConceptId
import com.paulchibamba.margin.domain.progression.ReadingOnlyChapters

class BookCompletionCalculator(
    private val readingOnlyChapters: ReadingOnlyChapters,
    private val matureDays: Int = DEFAULT_MATURE_DAYS,
) {

    fun completionOf(
        book: BookSlug,
        concepts: List<Concept>,
        progress: Map<ConceptId, ConceptProgress>,
    ): BookCompletion {
        val counted = concepts.filter { it.bookSlug == book && it !in readingOnlyChapters }
        val progressOfCounted = counted.map { concept -> progress[concept.id] ?: ConceptProgress.Untouched }
        return BookCompletion(
            bookSlug = book,
            introduced = progressOfCounted.count(ConceptProgress::isIntroduced),
            remembered = progressOfCounted.count(::isRemembered),
            total = counted.size,
        )
    }

    private fun isRemembered(progress: ConceptProgress): Boolean {
        val card = progress.card ?: return false
        return card.state == CardState.REVIEW && card.scheduledDays >= matureDays
    }

    companion object {
        const val DEFAULT_MATURE_DAYS = 21
    }
}
