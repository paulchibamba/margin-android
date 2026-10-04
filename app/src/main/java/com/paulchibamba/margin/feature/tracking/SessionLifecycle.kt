package com.paulchibamba.margin.feature.tracking

import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.paulchibamba.margin.domain.tracking.SessionEntry
import com.paulchibamba.margin.domain.tracking.SessionTracker
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@Singleton
class SessionLifecycle @Inject constructor(
    private val tracker: SessionTracker,
    @ApplicationContext private val context: Context,
) : DefaultLifecycleObserver {
    private val logFailure = CoroutineExceptionHandler { _, error -> Log.w(TAG, "Session tracking failed", error) }
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default.limitedParallelism(1) + logFailure)
    private val powerManager = context.getSystemService(PowerManager::class.java)

    @Volatile
    private var nextEntry = SessionEntry.LAUNCHER
    private var awayCheck: Job? = null
    private var idleChecks: Job? = null

    fun start() = ProcessLifecycleOwner.get().lifecycle.addObserver(this)

    fun noteLaunch(intent: Intent?) {
        nextEntry = EntryIntent.entryOf(intent)
    }

    fun onInput() {
        scope.launch { tracker.onInput() }
    }

    override fun onStart(owner: LifecycleOwner) {
        awayCheck?.cancel()
        val entry = nextEntry.also { nextEntry = SessionEntry.LAUNCHER }
        scope.launch { tracker.onForeground(entry) }
        idleChecks = scope.launch { checkIdleWhileForeground() }
    }

    override fun onStop(owner: LifecycleOwner) {
        idleChecks?.cancel()
        val isScreenOn = powerManager.isInteractive
        scope.launch { tracker.onBackground(isScreenOn) }
        awayCheck = scope.launch {
            delay(SessionTracker.AWAY_GRACE)
            tracker.endIfAway()
        }
    }

    private suspend fun checkIdleWhileForeground() {
        while (currentCoroutineContext().isActive) {
            delay(IDLE_CHECK_INTERVAL)
            tracker.endIfIdle()
        }
    }

    private companion object {
        const val TAG = "MarginSessions"
        val IDLE_CHECK_INTERVAL = 30.seconds
    }
}
