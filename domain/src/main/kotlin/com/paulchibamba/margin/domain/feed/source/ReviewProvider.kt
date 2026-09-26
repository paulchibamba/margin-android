package com.paulchibamba.margin.domain.feed.source

import com.paulchibamba.margin.domain.feed.Candidate
import com.paulchibamba.margin.domain.feed.CandidateProvider
import com.paulchibamba.margin.domain.feed.CandidateSource
import com.paulchibamba.margin.domain.feed.FeedState
import com.paulchibamba.margin.domain.feed.LearningLibrary
import com.paulchibamba.margin.domain.model.Concept
import com.paulchibamba.margin.domain.model.Post
import java.time.Instant

class ReviewProvider : CandidateProvider {

    override fun candidates(library: LearningLibrary, state: FeedState, now: Instant): List<Candidate> =
        library.conceptsInBookOrder
            .filter { concept -> isReadyForReview(concept, state, now) }
            .flatMap { concept -> testsToReview(concept, library, state) }
            .map { test -> Candidate(test, CandidateSource.REVIEW) }

    private fun isReadyForReview(concept: Concept, state: FeedState, now: Instant): Boolean {
        val progress = state.progressOf(concept)
        return progress.isIntroduced && progress.isDueForReview(now) && !progress.isAwaitingReteach
    }

    private fun testsToReview(concept: Concept, library: LearningLibrary, state: FeedState): List<Post> {
        val tests = library.testPostsOf(concept)
        return tests.filterNot { test -> state.hasSeen(test.id) }.ifEmpty { tests }
    }
}
