package com.paulchibamba.margin.feature.tracking

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.hilt.android.EntryPointAccessors

class RollupWorker(context: Context, parameters: WorkerParameters) : CoroutineWorker(context, parameters) {

    private val entryPoint =
        EntryPointAccessors.fromApplication(context.applicationContext, TrackingJobsEntryPoint::class.java)

    override suspend fun doWork(): Result {
        entryPoint.rollUpEvents().missedDays()
        return Result.success()
    }
}
