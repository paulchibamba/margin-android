package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.drop.DailyDrop
import com.paulchibamba.margin.domain.drop.DropStage
import com.paulchibamba.margin.domain.drop.DropStatus
import com.paulchibamba.margin.domain.repository.Clock
import com.paulchibamba.margin.domain.repository.DailyDropRepository
import com.paulchibamba.margin.domain.time.today
import com.paulchibamba.margin.domain.tracking.Event
import com.paulchibamba.margin.domain.tracking.EventRecorder
import javax.inject.Inject

class LeaveDropItem @Inject constructor(
    private val drops: DailyDropRepository,
    private val stateSource: FeedStateSource,
    private val recorder: EventRecorder,
    private val clock: Clock,
    private val lock: FeedStateLock,
) {
    suspend operator fun invoke(index: Int): DropStatus? = lock.withLock {
        val drop = drops.forDate(clock.today())?.takeIf { index in it.items.indices } ?: return@withLock null
        val left = drop.left(index)
        val status = DropStatus(left.doneCount(stateSource.current()), left.size)
        recorder.record(Event.DropEvent(DropStage.ITEM_DONE, index + 1, left.size))
        drops.save(if (status.isFinished) complete(left) else left)
        status
    }

    private fun complete(drop: DailyDrop): DailyDrop {
        if (drop.isCompleted) return drop
        recorder.record(Event.DropEvent(DropStage.COMPLETED, drop.size, drop.size))
        return drop.copy(completedAt = clock.now())
    }
}
