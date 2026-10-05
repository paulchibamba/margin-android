package com.paulchibamba.margin.domain.feed.filter

import com.paulchibamba.margin.domain.feed.Candidate
import com.paulchibamba.margin.domain.feed.CandidateSource
import com.paulchibamba.margin.domain.feed.cia
import com.paulchibamba.margin.domain.feed.freshState
import com.paulchibamba.margin.domain.feed.generatedPostOf
import com.paulchibamba.margin.domain.feed.historyEntryOf
import com.paulchibamba.margin.domain.feed.leastPrivilege
import com.paulchibamba.margin.domain.feed.memeOf
import com.paulchibamba.margin.domain.feed.tipOf
import com.paulchibamba.margin.domain.progress.RewardKind
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NoRewardKindRepeatTest {
    private val filter = NoRewardKindRepeat()
    private val lastComeback = generatedPostOf(RewardKind.Comeback, cia).toPost(cia.bookSlug)!!
    private val state = freshState.copy(
        history = listOf(
            historyEntryOf(lastComeback, step = 3, CandidateSource.REWARD),
            historyEntryOf(tipOf(cia), step = 4, CandidateSource.NEW),
        ),
    )

    private fun rewardOf(kind: RewardKind): Candidate {
        val post = generatedPostOf(kind, leastPrivilege, index = 1).toPost(leastPrivilege.bookSlug)!!
        return Candidate(post, CandidateSource.REWARD)
    }

    @Test
    fun `the same reward kind as the previous reward is held back`() {
        assertFalse(filter.keeps(rewardOf(RewardKind.Comeback), state))
    }

    @Test
    fun `a different reward kind, a meme or a non-reward post passes`() {
        assertTrue(filter.keeps(rewardOf(RewardKind.Quote), state))
        assertTrue(filter.keeps(Candidate(memeOf(leastPrivilege), CandidateSource.REWARD), state))
        assertTrue(filter.keeps(Candidate(tipOf(leastPrivilege), CandidateSource.NEW), state))
    }

    @Test
    fun `with no reward yet, every kind passes`() {
        assertTrue(filter.keeps(rewardOf(RewardKind.Comeback), freshState))
    }
}
