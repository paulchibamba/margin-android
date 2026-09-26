package com.paulchibamba.margin.domain.feed.source

import com.paulchibamba.margin.domain.feed.Candidate
import com.paulchibamba.margin.domain.feed.CandidateProvider
import com.paulchibamba.margin.domain.feed.CandidateSource
import com.paulchibamba.margin.domain.feed.FeedState
import com.paulchibamba.margin.domain.feed.LearningLibrary
import com.paulchibamba.margin.domain.model.Concept
import java.time.Instant

class ResurfaceProvider(private val quietSteps: Int = DEFAULT_QUIET_STEPS) : CandidateProvider {

    override fun candidates(library: LearningLibrary, state: FeedState, now: Instant): List<Candidate> =
        library.conceptsInBookOrder
            .filter { concept -> hasBeenQuiet(concept, state) }
            .flatMap(library::nonMemePostsOf)
            .map { post -> Candidate(post, CandidateSource.RESURFACE) }

    private fun hasBeenQuiet(concept: Concept, state: FeedState): Boolean {
        val progress = state.progressOf(concept)
        return progress.isIntroduced && state.step - (progress.lastShownStep ?: 0) > quietSteps
    }

    companion object {
        const val DEFAULT_QUIET_STEPS = 15
    }
}
