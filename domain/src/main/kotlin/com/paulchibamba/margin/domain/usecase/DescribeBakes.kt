package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.bake.BakeSummary
import com.paulchibamba.margin.domain.progress.GeneratedPost
import com.paulchibamba.margin.domain.repository.BakeStateStore
import com.paulchibamba.margin.domain.repository.Clock
import com.paulchibamba.margin.domain.repository.GeneratedPostRepository
import com.paulchibamba.margin.domain.repository.LlmLedger
import com.paulchibamba.margin.domain.time.startOfToday
import javax.inject.Inject

class DescribeBakes @Inject constructor(
    private val bakeState: BakeStateStore,
    private val generatedPosts: GeneratedPostRepository,
    private val ledger: LlmLedger,
    private val clock: Clock,
) {
    suspend operator fun invoke(): BakeSummary = BakeSummary(
        lastBakeAt = bakeState.load().lastBakeAt,
        spentToday = ledger.spendSince(clock.startOfToday()),
        recentPosts = generatedPosts.all().sortedByDescending(GeneratedPost::createdAt).take(RECENT_POSTS),
    )

    private companion object {
        const val RECENT_POSTS = 8
    }
}
