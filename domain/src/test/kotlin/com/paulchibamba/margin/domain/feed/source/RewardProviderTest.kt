package com.paulchibamba.margin.domain.feed.source

import com.paulchibamba.margin.domain.feed.*
import com.paulchibamba.margin.domain.progress.RewardKind
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RewardProviderTest {

    private val provider = RewardProvider()
    private val introduced = freshState.withIntroduced(cia, leastPrivilege)

    @Test
    fun `no meme arrives before the reward step`() {
        val state = introduced.copy(step = 4, rewardAtStep = 5)

        assertTrue(provider.candidates(libraryWith(), state, now).isEmpty())
    }

    @Test
    fun `from the reward step, unseen memes of introduced concepts are offered`() {
        val state = introduced.copy(step = 5, rewardAtStep = 5).withSeen(memeOf(leastPrivilege))

        val candidates = provider.candidates(libraryWith(), state, now)

        assertEquals(listOf(Candidate(memeOf(cia), CandidateSource.REWARD)), candidates)
    }

    @Test
    fun `from the reward step, fresh progress posts are offered too, but never re-explains`() {
        val comeback = generatedPostOf(RewardKind.Comeback, cia)
        val reExplain = generatedPostOf(RewardKind.ReExplain, leastPrivilege)
        val library = libraryWith(generatedPosts = listOf(comeback, reExplain))
        val state = introduced.copy(step = 5, rewardAtStep = 5).withSeen(memeOf(cia), memeOf(leastPrivilege))

        val candidates = provider.candidates(library, state, now)

        assertEquals(listOf(comeback.id), candidates.map { it.post.id })
        assertEquals(setOf(CandidateSource.REWARD), candidates.map { it.source }.toSet())
    }

    @Test
    fun `a progress post already shown is not offered again`() {
        val comeback = generatedPostOf(RewardKind.Comeback, cia)
        val library = libraryWith(generatedPosts = listOf(comeback))
        val state = introduced.copy(step = 5, rewardAtStep = 5).withSeen(memeOf(cia), memeOf(leastPrivilege))

        val shown = state.copy(seenPosts = state.seenPosts + (comeback.id to 3))

        assertTrue(provider.candidates(library, shown, now).isEmpty())
    }
}
