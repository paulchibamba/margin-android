package com.paulchibamba.margin.domain.feed.filter

import com.paulchibamba.margin.domain.feed.Candidate

data class FilterResult(val pool: List<Candidate>, val appliedFilters: List<String>)
