package com.paulchibamba.margin.domain.feed.source

import com.paulchibamba.margin.domain.feed.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ReviewProviderTest {

    private val provider = ReviewProvider()
    private val due = introducedProgress(due = now)

    @Test
    fun `a concept that is due offers its unseen tests`() {
        val state = freshState.withIntroduced(cia, progress = due).withSeen(mcqOf(cia))

        val candidates = provider.candidates(libraryWith(), state, now)

        assertEquals(listOf(Candidate(trueFalseOf(cia), CandidateSource.REVIEW)), candidates)
    }

    @Test
    fun `when every test has been seen, all of them are offered again`() {
        val state = freshState.withIntroduced(cia, progress = due).withSeen(mcqOf(cia), trueFalseOf(cia))

        val candidates = provider.candidates(libraryWith(), state, now)

        assertEquals(listOf(mcqOf(cia), trueFalseOf(cia)), candidates.map { it.post })
    }

    @Test
    fun `a concept that is not yet due is not reviewed`() {
        val state = freshState.withIntroduced(cia, progress = introducedProgress(due = now.plusSeconds(60)))

        assertTrue(provider.candidates(libraryWith(), state, now).isEmpty())
    }

    @Test
    fun `a lost concept is not quizzed until it has been re-taught`() {
        val lost = due.copy(confidence = Confidence.LOST, lostAtStep = 4)

        val awaiting = freshState.withIntroduced(cia, progress = lost)
        val retaught = freshState.withIntroduced(cia, progress = lost.copy(retaughtAtStep = 6))

        assertTrue(provider.candidates(libraryWith(), awaiting, now).isEmpty())
        assertEquals(2, provider.candidates(libraryWith(), retaught, now).size)
    }

    @Test
    fun `an inactive book still gets its reviews`() {
        val state = freshState.withIntroduced(promptInjection, progress = due)

        val candidates = provider.candidates(libraryWith(), state, now)

        assertEquals(listOf(mcqOf(promptInjection), trueFalseOf(promptInjection)), candidates.map { it.post })
    }
}
