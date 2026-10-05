package com.paulchibamba.margin.domain.feed.source

import com.paulchibamba.margin.domain.feed.Candidate
import com.paulchibamba.margin.domain.feed.CandidateProvider
import com.paulchibamba.margin.domain.feed.CandidateSource
import com.paulchibamba.margin.domain.feed.Confidence
import com.paulchibamba.margin.domain.feed.FeedState
import com.paulchibamba.margin.domain.feed.LearningLibrary
import com.paulchibamba.margin.domain.feed.ReteachSupport
import com.paulchibamba.margin.domain.model.Concept
import com.paulchibamba.margin.domain.model.Post
import java.time.Instant

class AngleProvider : CandidateProvider {

    override fun candidates(library: LearningLibrary, state: FeedState, now: Instant): List<Candidate> =
        library.conceptsInBookOrder
            .filter(state::isIntroduced)
            .flatMap { concept -> anglesOn(concept, library, state) }
            .distinctBy(Post::id)
            .map { post -> Candidate(post, CandidateSource.ANGLE) }

    private fun anglesOn(concept: Concept, library: LearningLibrary, state: FeedState): List<Post> =
        listOfNotNull(ReteachSupport.pendingFor(concept, library, state)) + usualAnglesOn(concept, library, state)

    private fun usualAnglesOn(concept: Concept, library: LearningLibrary, state: FeedState): List<Post> {
        if (state.progressOf(concept).confidence == Confidence.GOT) return emptyList()
        val unseenExplanations = library.teachPostsOf(concept).filterNot { state.hasSeen(it.id) }
        if (unseenExplanations.isNotEmpty() || !isLost(concept, state)) return unseenExplanations
        return listOfNotNull(bookWordsFallback(concept, library, state))
    }

    private fun isLost(concept: Concept, state: FeedState): Boolean =
        state.progressOf(concept).confidence == Confidence.LOST

    private fun bookWordsFallback(concept: Concept, library: LearningLibrary, state: FeedState): Post? =
        library.sourcePostFor(concept)?.takeUnless { source -> state.hasSeen(source.id) }
}
