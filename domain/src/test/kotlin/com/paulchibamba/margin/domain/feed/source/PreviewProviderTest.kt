package com.paulchibamba.margin.domain.feed.source

import com.paulchibamba.margin.domain.feed.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PreviewProviderTest {

    private val provider = PreviewProvider()

    @Test
    fun `with nothing read each active book teases one post of its first concept`() {
        val candidates = provider.candidates(libraryWith(), freshState, now)

        assertEquals(listOf(tipOf(cia), tipOf(sameOrigin)), candidates.map { it.post })
        assertTrue(candidates.all { it.source == CandidateSource.PREVIEW })
    }

    @Test
    fun `a teaser is a post not seen before`() {
        val candidates = provider.candidates(libraryWith(), freshState.withSeen(tipOf(cia)), now)

        assertEquals(mythOf(cia), candidates.first().post)
    }

    @Test
    fun `a concept with every teach post seen is not teased again`() {
        val state = freshState.withSeen(tipOf(cia), mythOf(cia))

        val candidates = provider.candidates(libraryWith(), state, now)

        assertTrue(candidates.none { it.post.conceptId == cia.id })
    }

    @Test
    fun `an unlocked concept is not a preview`() {
        val library = libraryWith(readNotes = setOf(noteId(appSec, 1, 0)))

        val candidates = provider.candidates(library, freshState, now)

        assertEquals(tipOf(leastPrivilege), candidates.first().post)
    }

    @Test
    fun `an inactive book is never previewed`() {
        val candidates = provider.candidates(libraryWith(), freshState, now)

        assertTrue(candidates.none { it.post.bookSlug == aiSecurity.slug })
    }
}
