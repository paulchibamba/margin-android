package com.paulchibamba.margin.feature.bake

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.paulchibamba.margin.domain.bake.BakeTrigger
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class BakeWorker(context: Context, parameters: WorkerParameters) : CoroutineWorker(context, parameters) {

    private val entryPoint = EntryPointAccessors.fromApplication(context.applicationContext, BakeEntryPoint::class.java)

    override suspend fun doWork(): Result {
        val trigger = triggerOf(inputData)
        val report = runCatching { oneAtATime.withLock { entryPoint.bakeProgressPosts()(trigger) } }
        report.onFailure { error -> Log.w(TAG, "Bake failed ($trigger)", error) }
        report.onSuccess { baked -> Log.i(TAG, "Bake $trigger: ${baked.rewards} posts, ${baked.reExplains} explains") }
        return Result.success()
    }

    companion object {
        private const val TAG = "MarginBake"
        private const val TRIGGER = "trigger"
        private val oneAtATime = Mutex()

        fun inputFor(trigger: BakeTrigger): Data = workDataOf(TRIGGER to trigger.name)

        private fun triggerOf(data: Data): BakeTrigger =
            data.getString(TRIGGER)?.let { name -> BakeTrigger.entries.firstOrNull { it.name == name } }
                ?: BakeTrigger.SCHEDULED
    }
}
