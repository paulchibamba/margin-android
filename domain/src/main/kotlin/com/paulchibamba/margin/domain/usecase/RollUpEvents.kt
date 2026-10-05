package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.model.NoteId
import com.paulchibamba.margin.domain.repository.Clock
import com.paulchibamba.margin.domain.repository.ContentRepository
import com.paulchibamba.margin.domain.repository.EventLog
import com.paulchibamba.margin.domain.repository.EventSink
import com.paulchibamba.margin.domain.repository.RollupStore
import com.paulchibamba.margin.domain.rollup.DailyRollup
import com.paulchibamba.margin.domain.rollup.DayEvents
import com.paulchibamba.margin.domain.rollup.RollupMetrics
import java.time.Instant
import java.time.LocalDate
import javax.inject.Inject
import kotlin.time.Duration

class RollUpEvents @Inject constructor(
    private val clock: Clock,
    private val sink: EventSink,
    private val log: EventLog,
    private val store: RollupStore,
    private val content: ContentRepository,
) {

    suspend fun today(): DailyRollup {
        sink.flush()
        return rollUp(dateOf(clock.now()), readingTimes())
    }

    suspend fun missedDays(): List<LocalDate> {
        sink.flush()
        val days = missedDaysUntil(dateOf(clock.now()).minusDays(1))
        if (days.isNotEmpty()) {
            val readingTimes = readingTimes()
            days.forEach { day -> rollUp(day, readingTimes) }
        }
        return days
    }

    private suspend fun missedDaysUntil(lastDay: LocalDate): List<LocalDate> {
        val firstDay = log.firstEventAt()?.let(::dateOf) ?: return emptyList()
        val computedTimes = store.computedTimes()
        return generateSequence(firstDay) { it.plusDays(1) }
            .takeWhile { !it.isAfter(lastDay) }
            .filter { day -> !isSettled(day, computedTimes[day]) }
            .toList()
    }

    private fun isSettled(day: LocalDate, computedAt: Instant?): Boolean =
        computedAt != null && !computedAt.isBefore(startOf(day.plusDays(1)))

    private suspend fun rollUp(date: LocalDate, readingTimes: Map<NoteId, Duration>): DailyRollup {
        val events = log.between(startOf(date), startOf(date.plusDays(1)))
        val metrics = RollupMetrics.of(DayEvents(events, clock.zone(), readingTimes))
        return DailyRollup(date, metrics, clock.now()).also { store.save(it) }
    }

    private suspend fun readingTimes(): Map<NoteId, Duration> =
        content.noteOutlines().associate { outline -> outline.id to outline.readingTime }

    private fun dateOf(instant: Instant): LocalDate = instant.atZone(clock.zone()).toLocalDate()

    private fun startOf(date: LocalDate): Instant = date.atStartOfDay(clock.zone()).toInstant()
}
