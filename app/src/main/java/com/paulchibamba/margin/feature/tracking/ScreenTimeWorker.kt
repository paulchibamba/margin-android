package com.paulchibamba.margin.feature.tracking

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.paulchibamba.margin.domain.screentime.ScreenTimeIngestion
import dagger.hilt.android.EntryPointAccessors

class ScreenTimeWorker(context: Context, parameters: WorkerParameters) : CoroutineWorker(context, parameters) {

    private val entryPoint =
        EntryPointAccessors.fromApplication(context.applicationContext, TrackingJobsEntryPoint::class.java)

    override suspend fun doWork(): Result = when (entryPoint.ingestScreenTime().missedDays()) {
        ScreenTimeIngestion.LOCKED -> Result.retry()
        ScreenTimeIngestion.DONE, ScreenTimeIngestion.NO_ACCESS -> Result.success()
    }
}
