package com.paulchibamba.margin.domain.feed.filter

import com.paulchibamba.margin.domain.feed.Candidate
import com.paulchibamba.margin.domain.feed.CandidateSource
import com.paulchibamba.margin.domain.feed.FeedState
import com.paulchibamba.margin.domain.progress.GeneratedPost
import com.paulchibamba.margin.domain.progress.RewardKind

class NoRewardKindRepeat : FeedFilter {
    override val name = "reward variety"

    override fun keeps(candidate: Candidate, state: FeedState): Boolean {
        val kind = candidate.post.rewardKind ?: return true
        return candidate.source != CandidateSource.REWARD || kind != lastRewardKindOf(state)
    }

    private fun lastRewardKindOf(state: FeedState): RewardKind? = state.history
        .lastOrNull { entry -> entry.source == CandidateSource.REWARD }
        ?.let { entry -> GeneratedPost.kindOf(entry.postId) }
}
