package com.paulchibamba.margin.domain.screentime

import java.time.Instant
import kotlin.time.Duration

object ForegroundTime {

    fun of(events: List<UsageEvent>, window: TimeWindow): Map<PackageName, Duration> {
        val tally = Tally(window)
        events.sortedBy(UsageEvent::at).filter { !it.at.isAfter(window.end) }.forEach(tally::accept)
        return tally.finish()
    }

    private class Tally(private val window: TimeWindow) {
        private val totals = mutableMapOf<PackageName, Duration>()
        private var foreground: PackageName? = null
        private var since: Instant = window.start

        fun accept(event: UsageEvent) {
            when (event) {
                is UsageEvent.Resumed -> resume(event.packageName, event.at)
                is UsageEvent.Paused -> pause(event.packageName, event.at)
                is UsageEvent.NothingInFront -> close(event.at)
            }
        }

        fun finish(): Map<PackageName, Duration> {
            close(window.end)
            return totals.filterValues(Duration::isPositive)
        }

        private fun resume(packageName: PackageName, at: Instant) {
            if (packageName == foreground) return
            close(at)
            foreground = packageName
            since = at
        }

        private fun pause(packageName: PackageName, at: Instant) {
            if (packageName == foreground) close(at)
        }

        private fun close(at: Instant) {
            val packageName = foreground ?: return
            totals[packageName] = (totals[packageName] ?: Duration.ZERO) + window.overlapOf(since, at)
            foreground = null
        }
    }
}
