package com.paulchibamba.margin.domain.feed

import java.time.Instant

fun interface CandidateProvider {
    fun candidates(library: LearningLibrary, state: FeedState, now: Instant): List<Candidate>
}
