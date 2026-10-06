package com.paulchibamba.margin.feature.drop

import android.util.Log
import com.paulchibamba.margin.domain.tracking.SessionTracker
import com.paulchibamba.margin.domain.usecase.LearnDropTime
import javax.inject.Inject
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class DropTimeJobs @Inject constructor(
    private val tracker: SessionTracker,
    private val learnDropTime: LearnDropTime,
) {
    private val logFailure = CoroutineExceptionHandler { _, error -> Log.w(TAG, "Couldn't learn the drop time", error) }
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO + logFailure)

    fun start() {
        relearn()
        tracker.addEndListener(::relearn)
    }

    private fun relearn() {
        scope.launch { learnDropTime() }
    }

    private companion object {
        const val TAG = "MarginDrop"
    }
}
