package com.paulchibamba.margin.domain.feed.source

import com.paulchibamba.margin.domain.feed.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DelightProviderTest {

    private val provider = DelightProvider()
    private val introduced = freshState.withIntroduced(cia, leastPrivilege)

    @Test
    fun `no meme arrives before the delight step`() {
        val state = introduced.copy(step = 4, delightAtStep = 5)

        assertTrue(provider.candidates(libraryWith(), state, now).isEmpty())
    }

    @Test
    fun `from the delight step, unseen memes of introduced concepts are offered`() {
        val state = introduced.copy(step = 5, delightAtStep = 5).withSeen(memeOf(leastPrivilege))

        val candidates = provider.candidates(libraryWith(), state, now)

        assertEquals(listOf(Candidate(memeOf(cia), CandidateSource.DELIGHT)), candidates)
    }
}
