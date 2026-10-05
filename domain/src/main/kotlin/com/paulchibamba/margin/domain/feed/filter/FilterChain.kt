package com.paulchibamba.margin.domain.feed.filter

import com.paulchibamba.margin.domain.feed.Candidate
import com.paulchibamba.margin.domain.feed.FeedConfig
import com.paulchibamba.margin.domain.feed.FeedState

class FilterChain(private val filters: List<FeedFilter>) {

    fun apply(candidates: List<Candidate>, state: FeedState): FilterResult =
        filters.fold(FilterResult(candidates, emptyList())) { result, filter -> applyOne(filter, result, state) }

    private fun applyOne(filter: FeedFilter, result: FilterResult, state: FeedState): FilterResult {
        val kept = result.pool.filter { candidate -> filter.keeps(candidate, state) }
        if (kept.isEmpty()) return result
        return FilterResult(kept, result.appliedFilters + filter.name)
    }

    companion object {
        fun from(config: FeedConfig) = FilterChain(
            listOf(
                NotRecentlyShown(config.recentlyShownWindow),
                NoConceptRepeat(config.noRepeatConcept),
                NoFormatRepeat(),
                NoRewardKindRepeat(),
                TestStreakCap(config.maxTestsInRow),
                PreviewSpacing(config.previewEvery),
            ),
        )
    }
}
