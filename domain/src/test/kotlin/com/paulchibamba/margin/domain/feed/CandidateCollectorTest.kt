package com.paulchibamba.margin.domain.feed

import kotlin.test.Test
import kotlin.test.assertEquals

class CandidateCollectorTest {

    @Test
    fun `the collector gathers every provider's candidates in order`() {
        val first = CandidateProvider { _, _, _ -> listOf(Candidate(tipOf(cia), CandidateSource.NEW)) }
        val second = CandidateProvider { _, _, _ -> listOf(Candidate(mcqOf(cia), CandidateSource.REVIEW)) }

        val candidates = CandidateCollector(listOf(first, second)).collect(libraryWith(), freshState, now)

        assertEquals(listOf(CandidateSource.NEW, CandidateSource.REVIEW), candidates.map { it.source })
    }

    @Test
    fun `with nothing read, the default providers offer only previews`() {
        val candidates = CandidateCollector.from(FeedConfig()).collect(libraryWith(), freshState, now)

        assertEquals(setOf(CandidateSource.PREVIEW), candidates.map { it.source }.toSet())
    }
}
