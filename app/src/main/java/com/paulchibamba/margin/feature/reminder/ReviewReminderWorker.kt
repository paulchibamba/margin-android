package com.paulchibamba.margin.feature.reminder

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.hilt.android.EntryPointAccessors

class ReviewReminderWorker(context: Context, parameters: WorkerParameters) : CoroutineWorker(context, parameters) {

    private val entryPoint =
        EntryPointAccessors.fromApplication(context.applicationContext, ReviewReminderEntryPoint::class.java)

    override suspend fun doWork(): Result {
        entryPoint.getReviewReminder()()?.let(entryPoint.notifier()::show)
        return Result.success()
    }
}
