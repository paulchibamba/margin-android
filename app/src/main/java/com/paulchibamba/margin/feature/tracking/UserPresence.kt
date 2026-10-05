package com.paulchibamba.margin.feature.tracking

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.PowerManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.paulchibamba.margin.domain.tracking.PostAttention
import com.paulchibamba.margin.domain.tracking.SessionTracker
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPresence @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sessions: SessionLifecycle,
    private val tracker: SessionTracker,
    private val attention: PostAttention,
) : DefaultLifecycleObserver {

    private val screenChanges = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            attention.onInteractiveChanged(intent.action == Intent.ACTION_SCREEN_ON)
        }
    }

    fun start() {
        tracker.addListener(attention)
        sessions.start()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        watchScreen()
    }

    fun onInput() {
        sessions.onInput()
        attention.onInput()
    }

    override fun onStart(owner: LifecycleOwner) = attention.onForegroundChanged(true)

    override fun onStop(owner: LifecycleOwner) = attention.onForegroundChanged(false)

    private fun watchScreen() {
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
        }
        ContextCompat.registerReceiver(context, screenChanges, filter, ContextCompat.RECEIVER_NOT_EXPORTED)
        attention.onInteractiveChanged(context.getSystemService(PowerManager::class.java).isInteractive)
    }
}
