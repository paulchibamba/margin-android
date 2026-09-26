package com.paulchibamba.margin.domain.feed.source

import com.paulchibamba.margin.domain.feed.Candidate
import com.paulchibamba.margin.domain.feed.CandidateProvider
import com.paulchibamba.margin.domain.feed.CandidateSource
import com.paulchibamba.margin.domain.feed.Confidence
import com.paulchibamba.margin.domain.feed.FeedState
import com.paulchibamba.margin.domain.feed.LearningLibrary
import com.paulchibamba.margin.domain.model.Concept
import com.paulchibamba.margin.domain.model.Post
import java.time.Instant

class AngleProvider : CandidateProvider {

    override fun candidates(library: LearningLibrary, state: FeedState, now: Instant): List<Candidate> =
        library.conceptsInBookOrder
            .filter { concept -> isStillBeingLearned(concept, state) }
            .flatMap { concept -> anglesOn(concept, library, state) }
            .map { post -> Candidate(post, CandidateSource.ANGLE) }

    private fun isStillBeingLearned(concept: Concept, state: FeedState): Boolean {
        val progress = state.progressOf(concept)
        return progress.isIntroduced && progress.confidence != Confidence.GOT
    }

    private fun anglesOn(concept: Concept, library: LearningLibrary, state: FeedState): List<Post> {
        val unseenExplanations = library.teachPostsOf(concept).filterNot { state.hasSeen(it.id) }
        if (unseenExplanations.isNotEmpty() || !isLost(concept, state)) return unseenExplanations
        return listOfNotNull(bookWordsFallback(concept, library, state))
    }

    private fun isLost(concept: Concept, state: FeedState): Boolean =
        state.progressOf(concept).confidence == Confidence.LOST

    private fun bookWordsFallback(concept: Concept, library: LearningLibrary, state: FeedState): Post? =
        library.sourcePostFor(concept)?.takeUnless { source -> state.hasSeen(source.id) }
}
