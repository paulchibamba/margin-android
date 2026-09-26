package com.paulchibamba.margin.domain.feed.source

import com.paulchibamba.margin.domain.feed.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NewConceptProviderTest {

    private val provider = NewConceptProvider()

    @Test
    fun `nothing is new before any reading`() {
        assertTrue(provider.candidates(libraryWith(), freshState, now).isEmpty())
    }

    @Test
    fun `the first unlocked concept of each active book offers its teach posts, except memes`() {
        val library = libraryWith(readNotes = setOf(noteId(appSec, 1, 0), noteId(grokking, 1, 0)))

        val candidates = provider.candidates(library, freshState, now)

        assertEquals(listOf(tipOf(cia), mythOf(cia), tipOf(sameOrigin), mythOf(sameOrigin)), candidates.map { it.post })
        assertTrue(candidates.all { it.source == CandidateSource.NEW })
    }

    @Test
    fun `concepts are introduced in book order, skipping those already introduced`() {
        val library = libraryWith(readNotes = setOf(noteId(appSec, 1, 5)))

        val candidates = provider.candidates(library, freshState.withIntroduced(cia), now)

        assertEquals(listOf(tipOf(leastPrivilege), mythOf(leastPrivilege)), candidates.map { it.post })
    }

    @Test
    fun `a reading-only chapter is skipped even after it has been read`() {
        val library = libraryWith(readNotes = setOf(noteId(appSec, 3, 0)))

        val candidates = provider.candidates(library, freshState.withIntroduced(cia, leastPrivilege), now)

        assertEquals(listOf(tipOf(defenceInDepth), mythOf(defenceInDepth)), candidates.map { it.post })
    }

    @Test
    fun `an inactive book never introduces new concepts`() {
        val candidates = provider.candidates(libraryWith(readNotes = readingEverything()), freshState, now)

        assertTrue(candidates.none { it.post.bookSlug == aiSecurity.slug })
    }
}
