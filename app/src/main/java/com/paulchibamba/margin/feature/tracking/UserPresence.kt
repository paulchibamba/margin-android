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
import com.paulchibamba.margin.domain.tracking.NoteAttention
import com.paulchibamba.margin.domain.tracking.PostAttention
import com.paulchibamba.margin.domain.tracking.PresenceListener
import com.paulchibamba.margin.domain.tracking.SessionTracker
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPresence @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sessions: SessionLifecycle,
    private val tracker: SessionTracker,
    private val postAttention: PostAttention,
    noteAttention: NoteAttention,
) : DefaultLifecycleObserver {
    private val listeners: List<PresenceListener> = listOf(postAttention, noteAttention)

    private val screenChanges = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val isOn = intent.action == Intent.ACTION_SCREEN_ON
            listeners.forEach { listener -> listener.onInteractiveChanged(isOn) }
        }
    }

    fun start() {
        tracker.addListener(postAttention)
        sessions.start()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        watchScreen()
    }

    fun onInput() {
        sessions.onInput()
        listeners.forEach(PresenceListener::onInput)
    }

    override fun onStart(owner: LifecycleOwner) = listeners.forEach { listener -> listener.onForegroundChanged(true) }

    override fun onStop(owner: LifecycleOwner) = listeners.forEach { listener -> listener.onForegroundChanged(false) }

    private fun watchScreen() {
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
        }
        ContextCompat.registerReceiver(context, screenChanges, filter, ContextCompat.RECEIVER_NOT_EXPORTED)
        val isOn = context.getSystemService(PowerManager::class.java).isInteractive
        listeners.forEach { listener -> listener.onInteractiveChanged(isOn) }
    }
}
