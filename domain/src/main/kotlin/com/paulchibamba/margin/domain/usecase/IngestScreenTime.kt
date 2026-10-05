package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.repository.Clock
import com.paulchibamba.margin.domain.repository.ScreenTimeStore
import com.paulchibamba.margin.domain.repository.UsageSource
import com.paulchibamba.margin.domain.screentime.AppCategory
import com.paulchibamba.margin.domain.screentime.AppScreenTime
import com.paulchibamba.margin.domain.screentime.ForegroundTime
import com.paulchibamba.margin.domain.screentime.PackageName
import com.paulchibamba.margin.domain.screentime.ScreenTimeIngestion
import com.paulchibamba.margin.domain.screentime.TimeWindow
import java.time.Instant
import java.time.LocalDate
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.toJavaDuration

class IngestScreenTime @Inject constructor(
    private val clock: Clock,
    private val source: UsageSource,
    private val store: ScreenTimeStore,
    private val rollUp: RollUpEvents,
) {

    suspend operator fun invoke(): ScreenTimeIngestion {
        val missed = missedDays()
        return if (missed == ScreenTimeIngestion.DONE) today() else missed
    }

    suspend fun missedDays(): ScreenTimeIngestion {
        if (!source.hasAccess()) return ScreenTimeIngestion.NO_ACCESS
        for (day in daysToIngest()) {
            if (!ingest(day, wholeDayOf(day))) return ScreenTimeIngestion.LOCKED
            store.markIngestedThrough(day)
        }
        return ScreenTimeIngestion.DONE
    }

    suspend fun today(): ScreenTimeIngestion {
        if (!source.hasAccess()) return ScreenTimeIngestion.NO_ACCESS
        val today = dateOf(clock.now())
        val isIngested = ingest(today, TimeWindow(startOf(today), clock.now()))
        return if (isIngested) ScreenTimeIngestion.DONE else ScreenTimeIngestion.LOCKED
    }

    private suspend fun daysToIngest(): List<LocalDate> {
        val yesterday = dateOf(clock.now()).minusDays(1)
        val earliest = yesterday.minusDays(BACKFILL_DAYS - 1)
        val first = listOfNotNull(earliest, store.ingestedThrough()?.plusDays(1), store.ingestFrom()).max()
        return generateSequence(first) { it.plusDays(1) }.takeWhile { !it.isAfter(yesterday) }.toList()
    }

    private suspend fun ingest(date: LocalDate, window: TimeWindow): Boolean {
        val lookback = TimeWindow(window.start.minus(LOOKBACK.toJavaDuration()), window.end)
        val events = source.events(lookback) ?: return false
        store.saveDay(date, appsOf(ForegroundTime.of(events, window)))
        rollUp.refreshScreenTime(listOf(date))
        return true
    }

    private suspend fun appsOf(foreground: Map<PackageName, Duration>): List<AppScreenTime> {
        val overrides = store.overrides()
        return foreground.map { (packageName, time) -> appOf(packageName, time, overrides[packageName]) }
    }

    private fun appOf(packageName: PackageName, foreground: Duration, override: Boolean?): AppScreenTime {
        val info = source.appInfo(packageName)
        val category = if (packageName == source.ownPackage) AppCategory.MARGIN else info.category
        val isDoom = category != AppCategory.MARGIN && (override ?: category.isDoomByDefault)
        return AppScreenTime(packageName, info.label, category, isDoom, foreground)
    }

    private fun wholeDayOf(date: LocalDate) = TimeWindow(startOf(date), startOf(date.plusDays(1)))

    private fun dateOf(instant: Instant): LocalDate = instant.atZone(clock.zone()).toLocalDate()

    private fun startOf(date: LocalDate): Instant = date.atStartOfDay(clock.zone()).toInstant()

    companion object {
        const val BACKFILL_DAYS = 7L
        val LOOKBACK = 12.hours
    }
}
