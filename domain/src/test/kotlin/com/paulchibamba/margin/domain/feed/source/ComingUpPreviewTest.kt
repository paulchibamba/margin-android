package com.paulchibamba.margin.domain.feed.source

import com.paulchibamba.margin.domain.feed.CandidateSource
import com.paulchibamba.margin.domain.feed.cia
import com.paulchibamba.margin.domain.feed.freshState
import com.paulchibamba.margin.domain.feed.generatedPostOf
import com.paulchibamba.margin.domain.feed.libraryWith
import com.paulchibamba.margin.domain.feed.now
import com.paulchibamba.margin.domain.progress.RewardKind
import kotlin.test.Test
import kotlin.test.assertEquals

class ComingUpPreviewTest {

    @Test
    fun `a coming up post for the previewed concept replaces the plain teaser`() {
        val comingUp = generatedPostOf(RewardKind.ComingUp, cia, note = cia.sourceNoteId)
        val library = libraryWith(generatedPosts = listOf(comingUp))

        val candidates = PreviewProvider().candidates(library, freshState, now).filter { it.post.conceptId == cia.id }

        assertEquals(listOf(comingUp.id), candidates.map { it.post.id })
        assertEquals(CandidateSource.PREVIEW, candidates.single().source)
    }

    @Test
    fun `without a coming up post the teaser stays`() {
        val candidates = PreviewProvider().candidates(libraryWith(), freshState, now)
            .filter { it.post.conceptId == cia.id }

        assertEquals(1, candidates.size)
        assertEquals(null, candidates.single().post.rewardKind)
    }
}
