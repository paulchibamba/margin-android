package com.paulchibamba.margin.domain.feed.source

import com.paulchibamba.margin.domain.feed.*
import com.paulchibamba.margin.domain.model.PostContent
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AngleProviderTest {

    private val provider = AngleProvider()
    private val lost = introducedProgress().copy(confidence = Confidence.LOST, lostAtStep = 3)
    private val sourceNote =
        sourceNoteOf(cia, "<h3>The Security Mandate</h3><p>Protect <b>confidentiality</b> &amp; integrity.</p>")

    @Test
    fun `an introduced concept offers its unseen explanations, except memes`() {
        val state = freshState.withIntroduced(cia).withSeen(tipOf(cia))

        val candidates = provider.candidates(libraryWith(), state, now)

        assertEquals(listOf(Candidate(mythOf(cia), CandidateSource.ANGLE)), candidates)
    }

    @Test
    fun `a concept marked got it gets no more explanations`() {
        val state = freshState.withIntroduced(cia, progress = introducedProgress().copy(confidence = Confidence.GOT))

        assertTrue(provider.candidates(libraryWith(), state, now).isEmpty())
    }

    @Test
    fun `a lost concept with every explanation used falls back to the book's own words`() {
        val state = freshState.withIntroduced(cia, progress = lost).withSeen(tipOf(cia), mythOf(cia))

        val candidate = provider.candidates(libraryWith(sourceNotes = listOf(sourceNote)), state, now).single()

        val source = candidate.post.content as PostContent.Source
        assertEquals("Section of ${cia.title}", source.title)
        assertEquals("The Security Mandate Protect confidentiality & integrity.", source.excerpt)
        assertEquals(cia.sourceNoteId, source.noteId)
    }

    @Test
    fun `the book's own words are offered only once`() {
        val library = libraryWith(sourceNotes = listOf(sourceNote))
        val sourcePost = library.sourcePostFor(cia)!!
        val state = freshState.withIntroduced(cia, progress = lost).withSeen(tipOf(cia), mythOf(cia), sourcePost)

        assertTrue(provider.candidates(library, state, now).isEmpty())
    }

    @Test
    fun `a concept that is not lost gets no fallback once its explanations are used`() {
        val state = freshState.withIntroduced(cia).withSeen(tipOf(cia), mythOf(cia))

        assertTrue(provider.candidates(libraryWith(sourceNotes = listOf(sourceNote)), state, now).isEmpty())
    }
}
