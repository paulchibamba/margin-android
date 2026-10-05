package com.paulchibamba.margin.domain.feed.source

import com.paulchibamba.margin.domain.feed.Candidate
import com.paulchibamba.margin.domain.feed.CandidateProvider
import com.paulchibamba.margin.domain.feed.CandidateSource
import com.paulchibamba.margin.domain.feed.FeedState
import com.paulchibamba.margin.domain.feed.LearningLibrary
import com.paulchibamba.margin.domain.model.Post
import java.time.Instant

class RewardProvider : CandidateProvider {

    override fun candidates(library: LearningLibrary, state: FeedState, now: Instant): List<Candidate> {
        if (!state.isRewardDue) return emptyList()
        return (memesOfIntroducedConcepts(library, state) + library.rewardPosts)
            .filterNot { post -> state.hasSeen(post.id) }
            .map { post -> Candidate(post, CandidateSource.REWARD) }
    }

    private fun memesOfIntroducedConcepts(library: LearningLibrary, state: FeedState): List<Post> =
        library.conceptsInBookOrder.filter(state::isIntroduced).flatMap(library::memesOf)
}
