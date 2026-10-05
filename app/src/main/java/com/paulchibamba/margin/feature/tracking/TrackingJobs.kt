package com.paulchibamba.margin.feature.tracking

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ListenableWorker
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.paulchibamba.margin.feature.reminder.ReminderTime
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalTime
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class TrackingJobs @Inject constructor(@ApplicationContext private val context: Context) {

    fun schedule() {
        enqueue(ROLLUP_WORK, RollupWorker::class.java, everyDays = 1, at = LocalTime.of(0, 30))
        enqueue(RETENTION_WORK, EventRetentionWorker::class.java, everyDays = 7, at = LocalTime.of(1, 0))
        enqueue(SCREEN_TIME_WORK, ScreenTimeWorker::class.java, everyDays = 1, at = LocalTime.of(0, 45))
    }

    private fun enqueue(name: String, worker: Class<out ListenableWorker>, everyDays: Long, at: LocalTime) {
        val request = PeriodicWorkRequest.Builder(worker, everyDays, TimeUnit.DAYS)
            .setInitialDelay(ReminderTime.delayUntilNext(at, ZonedDateTime.now()))
            .setBackoffCriteria(BackoffPolicy.LINEAR, RETRY_MINUTES, TimeUnit.MINUTES)
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(name, ExistingPeriodicWorkPolicy.KEEP, request)
    }

    private companion object {
        const val ROLLUP_WORK = "daily_rollup"
        const val RETENTION_WORK = "event_retention"
        const val SCREEN_TIME_WORK = "screen_time"
        const val RETRY_MINUTES = 30L
    }
}
