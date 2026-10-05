package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.rollup.DailyRollup
import com.paulchibamba.margin.domain.rollup.DayEvents
import com.paulchibamba.margin.domain.rollup.FakeRollupStore
import com.paulchibamba.margin.domain.rollup.RollupMetrics
import com.paulchibamba.margin.domain.rollup.ScreenTimeMetrics
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest

class ObserveScreenTimeTest {
    private val clock = FixedClock(Instant.parse("2026-10-07T09:00:00Z"))
    private val store = FakeRollupStore()
    private val observe = ObserveScreenTime(store, clock)
    private val emptyDay = RollupMetrics.of(DayEvents(emptyList(), ZoneOffset.UTC))

    private suspend fun saveDay(date: String, margin: Duration, doom: Duration) {
        val screenTime = ScreenTimeMetrics(screen = margin + doom, doom = doom, margin = margin, emptyList())
        store.save(DailyRollup(LocalDate.parse(date), emptyDay.copy(screenTime = screenTime), clock.instant))
    }

    @Test
    fun `days without screen time give no report`() = runTest {
        store.save(DailyRollup(LocalDate.parse("2026-10-06"), emptyDay, clock.instant))

        assertNull(observe().first())
    }

    @Test
    fun `the week adds up margin and doom minutes over the last seven days`() = runTest {
        saveDay("2026-09-30", margin = 60.minutes, doom = 60.minutes)
        saveDay("2026-10-01", margin = 10.minutes, doom = 30.minutes)
        saveDay("2026-10-07", margin = 20.minutes, doom = 40.minutes)

        val report = observe().first()!!

        assertEquals(listOf("2026-10-01", "2026-10-07").map(LocalDate::parse), report.days.map(ScreenTimeDay::date))
        assertEquals(30.minutes, report.margin)
        assertEquals(70.minutes, report.doom)
        assertEquals(0.3, report.marginShare)
    }
}
