package com.paulchibamba.margin.domain.feed.source

import com.paulchibamba.margin.domain.feed.Candidate
import com.paulchibamba.margin.domain.feed.CandidateProvider
import com.paulchibamba.margin.domain.feed.CandidateSource
import com.paulchibamba.margin.domain.feed.FeedState
import com.paulchibamba.margin.domain.feed.LearningLibrary
import com.paulchibamba.margin.domain.model.Book
import com.paulchibamba.margin.domain.model.Concept
import java.time.Instant

class PreviewProvider : CandidateProvider {

    override fun candidates(library: LearningLibrary, state: FeedState, now: Instant): List<Candidate> =
        library.activeBooks.mapNotNull { book -> teaserFrom(book, library, state) }

    private fun teaserFrom(book: Book, library: LearningLibrary, state: FeedState): Candidate? {
        val concept = conceptComingUp(book, library, state) ?: return null
        val unseenPost = library.teachPostsOf(concept).firstOrNull { post -> !state.hasSeen(post.id) } ?: return null
        return Candidate(unseenPost, CandidateSource.PREVIEW)
    }

    private fun conceptComingUp(book: Book, library: LearningLibrary, state: FeedState): Concept? =
        library.conceptsOf(book.slug)
            .filterNot(state::isIntroduced)
            .filterNot(library::isReadingOnly)
            .firstOrNull { concept -> !library.isUnlocked(concept) && library.isInPreviewWindow(concept) }
}
