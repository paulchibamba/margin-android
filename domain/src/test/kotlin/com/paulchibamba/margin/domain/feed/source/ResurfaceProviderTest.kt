package com.paulchibamba.margin.domain.feed.source

import com.paulchibamba.margin.domain.feed.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ResurfaceProviderTest {

    private val provider = ResurfaceProvider()
    private val shownAtStepOne = freshState.withIntroduced(cia)

    @Test
    fun `a concept shown within the last fifteen posts does not resurface`() {
        assertTrue(provider.candidates(libraryWith(), shownAtStepOne.copy(step = 16), now).isEmpty())
    }

    @Test
    fun `a concept not shown for more than fifteen posts resurfaces with every post but its meme`() {
        val candidates = provider.candidates(libraryWith(), shownAtStepOne.copy(step = 17), now)

        assertEquals(listOf(tipOf(cia), mythOf(cia), mcqOf(cia), trueFalseOf(cia)), candidates.map { it.post })
        assertTrue(candidates.all { it.source == CandidateSource.RESURFACE })
    }
}
