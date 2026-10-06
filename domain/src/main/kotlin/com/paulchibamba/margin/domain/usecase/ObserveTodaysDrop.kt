package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.drop.DropStatus
import com.paulchibamba.margin.domain.feed.FeedState
import com.paulchibamba.margin.domain.repository.Clock
import com.paulchibamba.margin.domain.repository.DailyDropRepository
import com.paulchibamba.margin.domain.repository.ProgressRepository
import com.paulchibamba.margin.domain.time.today
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class ObserveTodaysDrop @Inject constructor(
    private val drops: DailyDropRepository,
    private val progress: ProgressRepository,
    private val clock: Clock,
) {
    operator fun invoke(): Flow<DropStatus?> =
        combine(drops.observe(clock.today()), progress.observeFeedState()) { drop, state ->
            drop?.let { DropStatus(it.doneCount(state ?: FeedState(rewardAtStep = 0)), it.size) }
        }
}
