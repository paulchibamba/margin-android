package com.paulchibamba.margin.domain.feed.source

import com.paulchibamba.margin.domain.feed.Candidate
import com.paulchibamba.margin.domain.feed.CandidateProvider
import com.paulchibamba.margin.domain.feed.CandidateSource
import com.paulchibamba.margin.domain.feed.FeedState
import com.paulchibamba.margin.domain.feed.LearningLibrary
import java.time.Instant

class DelightProvider : CandidateProvider {

    override fun candidates(library: LearningLibrary, state: FeedState, now: Instant): List<Candidate> {
        if (!state.isDelightDue) return emptyList()
        return library.conceptsInBookOrder
            .filter(state::isIntroduced)
            .flatMap(library::memesOf)
            .filterNot { meme -> state.hasSeen(meme.id) }
            .map { meme -> Candidate(meme, CandidateSource.DELIGHT) }
    }
}
