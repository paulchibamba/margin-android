package com.paulchibamba.margin.feature.bake

import android.util.Log
import com.paulchibamba.margin.domain.tracking.SessionTracker
import com.paulchibamba.margin.domain.usecase.ObserveDropTime
import java.time.LocalTime
import javax.inject.Inject
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.withIndex
import kotlinx.coroutines.launch

class BakeJobs @Inject constructor(
    private val scheduler: BakeScheduler,
    private val tracker: SessionTracker,
    private val observeDropTime: ObserveDropTime,
) {
    private val logFailure = CoroutineExceptionHandler { _, error -> Log.w(TAG, "Couldn't schedule bakes", error) }
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO + logFailure)

    fun start() {
        scope.launch { followDropTime() }
        tracker.addEndListener(scheduler::bakeAfterSession)
    }

    private suspend fun followDropTime() {
        observeDropTime().withIndex().collect { (index, dropTime) ->
            val bakeAt = bakeTimeBefore(dropTime)
            if (index == 0) scheduler.scheduleDaily(bakeAt) else scheduler.rescheduleDaily(bakeAt)
        }
    }

    private fun bakeTimeBefore(dropTime: LocalTime): LocalTime = dropTime.minusHours(BAKE_LEAD_HOURS)

    private companion object {
        const val TAG = "MarginBake"
        const val BAKE_LEAD_HOURS = 2L
    }
}
