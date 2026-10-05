package com.paulchibamba.margin.domain.progress

import com.paulchibamba.margin.domain.feed.ConceptProgress
import com.paulchibamba.margin.domain.memory.CardState
import com.paulchibamba.margin.domain.memory.Rating
import com.paulchibamba.margin.domain.model.BookSlug
import com.paulchibamba.margin.domain.model.Concept
import com.paulchibamba.margin.domain.model.ConceptId
import com.paulchibamba.margin.domain.model.NoteId
import com.paulchibamba.margin.domain.model.NoteOutline
import com.paulchibamba.margin.domain.model.NotePosition
import com.paulchibamba.margin.domain.model.Post
import com.paulchibamba.margin.domain.rewards.BookCompletionCalculator
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

internal class ProgressContext(val sources: ProgressSources, val now: Instant, val zone: ZoneId) {
    val today: LocalDate = dateOf(now)
    val countedConcepts: List<Concept> = sources.books.flatMap { book -> countedConceptsOf(book.slug) }
    val postsByConcept: Map<ConceptId, List<Post>> = sources.posts.groupBy(Post::conceptId)
    val conceptsByNote: Map<NoteId, List<Concept>> = countedConcepts
        .filter { concept -> concept.sourceNoteId != null }
        .groupBy { concept -> concept.sourceNoteId!! }
    private val conceptsById = sources.concepts.associateBy(Concept::id)
    private val bookTitles = sources.books.associate { book -> book.slug to book.title }
    private val readingProgress = sources.reading.progressWith(sources.noteOutlines)

    fun dateOf(instant: Instant): LocalDate = instant.atZone(zone).toLocalDate()

    fun conceptsOn(note: NoteOutline): List<Concept> = conceptsByNote[note.id].orEmpty()

    fun conceptOf(id: ConceptId): Concept? = conceptsById[id]

    fun bookTitleOf(book: BookSlug): String = bookTitles[book].orEmpty()

    fun frontierOf(book: BookSlug): NotePosition? = readingProgress.frontierOf(book)

    fun progressOf(concept: Concept): ConceptProgress = sources.feedState.progressOf(concept)

    fun isIntroduced(concept: Concept): Boolean = progressOf(concept).isIntroduced

    fun isRemembered(concept: Concept): Boolean {
        val card = progressOf(concept).card ?: return false
        return card.state == CardState.REVIEW && card.scheduledDays >= BookCompletionCalculator.DEFAULT_MATURE_DAYS
    }

    fun hasPassedSince(concept: Concept, since: Instant): Boolean = sources.reviews.any { review ->
        review.conceptId == concept.id && review.rating != Rating.AGAIN && review.at.isAfter(since)
    }

    private fun countedConceptsOf(book: BookSlug): List<Concept> = sources.concepts
        .filter { concept -> concept.bookSlug == book && concept !in sources.readingOnlyChapters }
        .sortedWith(compareBy(Concept::chapter, Concept::order))
}
