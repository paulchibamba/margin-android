package com.paulchibamba.margin.feature.bake

import com.paulchibamba.margin.domain.tracking.SessionTracker
import javax.inject.Inject

class BakeJobs @Inject constructor(private val scheduler: BakeScheduler, private val tracker: SessionTracker) {

    fun start() {
        scheduler.scheduleDaily()
        tracker.addEndListener(scheduler::bakeAfterSession)
    }
}
