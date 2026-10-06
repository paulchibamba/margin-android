package com.paulchibamba.margin.domain.bake

import com.paulchibamba.margin.domain.llm.LlmSpend
import com.paulchibamba.margin.domain.progress.GeneratedPost
import java.time.Instant

data class BakeSummary(val lastBakeAt: Instant?, val spentToday: LlmSpend, val recentPosts: List<GeneratedPost>)
