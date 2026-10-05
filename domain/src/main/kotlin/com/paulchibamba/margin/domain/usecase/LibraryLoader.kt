package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.feed.LearningLibrary
import com.paulchibamba.margin.domain.model.Concept
import com.paulchibamba.margin.domain.model.ConceptId
import com.paulchibamba.margin.domain.progress.ProgressFactsCalculator
import com.paulchibamba.margin.domain.progress.StruggleDetector
import com.paulchibamba.margin.domain.progress.StruggleTrigger
import com.paulchibamba.margin.domain.repository.Clock
import com.paulchibamba.margin.domain.repository.ContentRepository
import com.paulchibamba.margin.domain.repository.EventLog
import com.paulchibamba.margin.domain.repository.GeneratedPostRepository
import com.paulchibamba.margin.domain.repository.ProgressRepository
import com.paulchibamba.margin.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class LibraryLoader @Inject constructor(
    private val content: ContentRepository,
    private val progress: ProgressRepository,
    private val settings: SettingsRepository,
    private val generatedPosts: GeneratedPostRepository,
    private val eventLog: EventLog,
    private val clock: Clock,
) {
    suspend fun load(): LearningLibrary {
        val concepts = content.concepts()
        return LearningLibrary(
            books = content.books(),
            concepts = concepts,
            posts = content.posts(),
            sourceNotes = content.notes(concepts.mapNotNull(Concept::sourceNoteId)).associateBy { it.id },
            bookSettings = settings.bookSettings(),
            readingOnlyChapters = settings.readingOnlyChapters(),
            readingProgress = progress.reading().progressWith(content.noteOutlines()),
            generatedPosts = generatedPosts.fresh(clock.now()),
            strugglingConcepts = strugglingConceptsAmong(concepts),
        )
    }

    private suspend fun strugglingConceptsAmong(concepts: List<Concept>): Set<ConceptId> {
        val now = clock.now()
        val events = eventLog.between(ProgressFactsCalculator.windowStart(now), now)
        val reviews = progress.observeReviews().first()
        val actions = progress.observeActions().first()
        return StruggleDetector().strugglesOf(concepts, reviews, actions, events, clock.zone(), now)
            .filter { struggle -> struggle.trigger != StruggleTrigger.LOST }
            .map { struggle -> struggle.concept.id }
            .toSet()
    }
}
