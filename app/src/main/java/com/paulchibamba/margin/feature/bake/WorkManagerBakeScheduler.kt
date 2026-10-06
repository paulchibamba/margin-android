package com.paulchibamba.margin.feature.bake

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.paulchibamba.margin.domain.bake.BakeTrigger
import com.paulchibamba.margin.feature.reminder.ReminderTime
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalTime
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class WorkManagerBakeScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
) : BakeScheduler {

    private val workManager get() = WorkManager.getInstance(context)
    private val batteryNotLow = Constraints.Builder().setRequiresBatteryNotLow(true).build()

    override fun scheduleDaily() {
        val request = PeriodicWorkRequestBuilder<BakeWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(ReminderTime.delayUntilNext(DAILY_BAKE_TIME, ZonedDateTime.now()))
            .setConstraints(batteryNotLow)
            .setInputData(BakeWorker.inputFor(BakeTrigger.SCHEDULED))
            .build()
        workManager.enqueueUniquePeriodicWork(DAILY_WORK, ExistingPeriodicWorkPolicy.KEEP, request)
    }

    override fun bakeAfterSession() {
        val request = OneTimeWorkRequestBuilder<BakeWorker>()
            .setConstraints(batteryNotLow)
            .setInputData(BakeWorker.inputFor(BakeTrigger.SESSION_END))
            .build()
        workManager.enqueueUniqueWork(SESSION_WORK, ExistingWorkPolicy.KEEP, request)
    }

    override fun bakeReExplainsNow() {
        val request = OneTimeWorkRequestBuilder<BakeWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setInputData(BakeWorker.inputFor(BakeTrigger.RE_EXPLAIN))
            .build()
        workManager.enqueueUniqueWork(RE_EXPLAIN_WORK, ExistingWorkPolicy.KEEP, request)
    }

    private companion object {
        val DAILY_BAKE_TIME: LocalTime = LocalTime.of(6, 30)
        const val DAILY_WORK = "bake_daily"
        const val SESSION_WORK = "bake_after_session"
        const val RE_EXPLAIN_WORK = "bake_re_explains"
    }
}
