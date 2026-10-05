package com.paulchibamba.margin.domain.tracking

import com.paulchibamba.margin.domain.repository.Clock
import java.time.Instant
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration
import kotlin.time.toKotlinDuration

class AttentionTracker(private val clock: Clock, private val idleAfter: Duration = IDLE_AFTER) {
    private var isSettled = false
    private var isForeground = true
    private var isInteractive = true
    private var lastInputAt: Instant = clock.now()
    private var countedUntil: Instant = clock.now()
    private var active = Duration.ZERO
    private var idle = Duration.ZERO

    fun restart() {
        countedUntil = clock.now()
        lastInputAt = countedUntil
        active = Duration.ZERO
        idle = Duration.ZERO
    }

    fun onInput() {
        count()
        lastInputAt = clock.now()
    }

    fun setSettled(settled: Boolean) {
        count()
        isSettled = settled
    }

    fun setForeground(foreground: Boolean) {
        count()
        if (foreground && !isForeground) lastInputAt = clock.now()
        isForeground = foreground
    }

    fun setInteractive(interactive: Boolean) {
        count()
        isInteractive = interactive
    }

    fun totals(): AttentionTotals {
        count()
        return AttentionTotals(active, idle)
    }

    private fun count() {
        val now = clock.now()
        if (isSettled && isForeground && isInteractive) addInterval(countedUntil, now)
        countedUntil = now
    }

    private fun addInterval(from: Instant, to: Instant) {
        val activeUntil = minOf(to, lastInputAt + idleAfter.toJavaDuration())
        val attended = between(from, activeUntil).coerceAtLeast(Duration.ZERO)
        active += attended
        idle += between(from, to) - attended
    }

    private fun between(from: Instant, to: Instant) = java.time.Duration.between(from, to).toKotlinDuration()

    companion object {
        val IDLE_AFTER = 20.seconds
    }
}
