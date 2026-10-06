package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.drop.DropStage
import com.paulchibamba.margin.domain.repository.Clock
import com.paulchibamba.margin.domain.repository.DailyDropRepository
import com.paulchibamba.margin.domain.time.today
import com.paulchibamba.margin.domain.tracking.Event
import com.paulchibamba.margin.domain.tracking.EventRecorder
import javax.inject.Inject

class EndDropVisit @Inject constructor(
    private val drops: DailyDropRepository,
    private val stateSource: FeedStateSource,
    private val recorder: EventRecorder,
    private val clock: Clock,
    private val lock: FeedStateLock,
) {
    suspend fun continueIntoFeed() = lock.withLock {
        val drop = drops.forDate(clock.today()) ?: return@withLock
        drops.save(drop.copy(isContinuedIntoFeed = true))
        recorder.record(Event.DropEvent(DropStage.CONTINUED, drop.size, drop.size))
    }

    suspend fun dismiss() = lock.withLock {
        val drop = drops.forDate(clock.today()) ?: return@withLock
        recorder.record(Event.DropEvent(DropStage.DISMISSED, drop.doneCount(stateSource.current()), drop.size))
    }
}
