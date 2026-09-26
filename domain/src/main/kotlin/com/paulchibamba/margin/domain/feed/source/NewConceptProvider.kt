package com.paulchibamba.margin.domain.feed.source

import com.paulchibamba.margin.domain.feed.Candidate
import com.paulchibamba.margin.domain.feed.CandidateProvider
import com.paulchibamba.margin.domain.feed.CandidateSource
import com.paulchibamba.margin.domain.feed.FeedState
import com.paulchibamba.margin.domain.feed.LearningLibrary
import com.paulchibamba.margin.domain.model.Book
import com.paulchibamba.margin.domain.model.Concept
import java.time.Instant

class NewConceptProvider : CandidateProvider {

    override fun candidates(library: LearningLibrary, state: FeedState, now: Instant): List<Candidate> =
        library.activeBooks.flatMap { book -> candidatesFrom(book, library, state) }

    private fun candidatesFrom(book: Book, library: LearningLibrary, state: FeedState): List<Candidate> {
        val next = nextUnlockedConcept(book, library, state) ?: return emptyList()
        return library.teachPostsOf(next).map { post -> Candidate(post, CandidateSource.NEW) }
    }

    private fun nextUnlockedConcept(book: Book, library: LearningLibrary, state: FeedState): Concept? =
        library.conceptsOf(book.slug)
            .filterNot(state::isIntroduced)
            .filterNot(library::isReadingOnly)
            .firstOrNull(library::isUnlocked)
}
