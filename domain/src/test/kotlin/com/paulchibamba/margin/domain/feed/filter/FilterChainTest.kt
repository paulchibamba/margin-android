package com.paulchibamba.margin.domain.feed.filter

import com.paulchibamba.margin.domain.feed.*
import kotlin.test.Test
import kotlin.test.assertEquals

class FilterChainTest {

    private val chain = FilterChain.from(FeedConfig())

    @Test
    fun `filters narrow the pool and report which ones applied`() {
        val state = freshState.afterShowing(tipOf(sameOrigin))
        val candidates = listOf(Candidate(tipOf(cia), CandidateSource.NEW), Candidate(mythOf(cia), CandidateSource.NEW))

        val result = chain.apply(candidates, state)

        assertEquals(listOf(Candidate(mythOf(cia), CandidateSource.NEW)), result.pool)
        assertEquals(
            listOf("not shown recently", "concept spacing", "format variety", "tests in a row", "preview spacing"),
            result.appliedFilters,
        )
    }

    @Test
    fun `a filter that would leave nothing is skipped rather than showing nothing`() {
        val state = freshState.afterShowing(tipOf(sameOrigin))
        val onlyTips = listOf(Candidate(tipOf(cia), CandidateSource.NEW))

        val result = chain.apply(onlyTips, state)

        assertEquals(onlyTips, result.pool)
        assertEquals(false, "format variety" in result.appliedFilters)
    }
}
