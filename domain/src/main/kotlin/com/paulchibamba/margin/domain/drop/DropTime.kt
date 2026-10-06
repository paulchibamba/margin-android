package com.paulchibamba.margin.domain.drop

import com.paulchibamba.margin.domain.tracking.SessionEntry
import com.paulchibamba.margin.domain.tracking.SessionStarted
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

object DropTime {
    val FALLBACK: LocalTime = LocalTime.of(8, 30)
    const val MINIMUM_SESSIONS = 5
    const val WINDOW_DAYS = 14L
    private const val SLOT_MINUTES = 30

    fun windowStart(today: LocalDate): LocalDate = today.minusDays(WINDOW_DAYS - 1)

    fun learnedSlot(sessionStarts: List<SessionStarted>, today: LocalDate, zone: ZoneId): LearnedSlot {
        val startTimes = sessionStarts.filter(::isUserChosen).map { it.at.atZone(zone).toLocalDateTime() }
            .filter { it.toLocalDate() in windowStart(today)..today }
            .map { it.toLocalTime() }
        if (startTimes.size < MINIMUM_SESSIONS) return LearnedSlot(FALLBACK, 0, startTimes.size)
        val (slot, count) = modalSlot(startTimes)
        return LearnedSlot(slot, count, startTimes.size)
    }

    private fun isUserChosen(start: SessionStarted) = start.entry != SessionEntry.DROP_NOTIFICATION

    private fun modalSlot(startTimes: List<LocalTime>): Pair<LocalTime, Int> {
        val countsBySlot = startTimes.groupingBy(::slotOf).eachCount()
        val highest = countsBySlot.values.max()
        val earliest = countsBySlot.filterValues { it == highest }.keys.min()
        return earliest to highest
    }

    private fun slotOf(time: LocalTime): LocalTime {
        val minuteOfDay = time.hour * 60 + time.minute
        val slotMinute = minuteOfDay - minuteOfDay % SLOT_MINUTES
        return LocalTime.of(slotMinute / 60, slotMinute % 60)
    }
}
