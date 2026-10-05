package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.rollup.FakeRollupStore
import com.paulchibamba.margin.domain.screentime.AppCategory
import com.paulchibamba.margin.domain.screentime.AppInfo
import com.paulchibamba.margin.domain.screentime.FakeScreenTimeStore
import com.paulchibamba.margin.domain.screentime.FakeUsageSource
import com.paulchibamba.margin.domain.screentime.PackageName
import com.paulchibamba.margin.domain.screentime.ScreenTimeIngestion
import com.paulchibamba.margin.domain.screentime.UsageEvent
import com.paulchibamba.margin.domain.tracking.FakeEventLog
import com.paulchibamba.margin.domain.tracking.RecordingEventSink
import java.time.Instant
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.minutes
import kotlinx.coroutines.test.runTest

class IngestScreenTimeTest {
    private val clock = FixedClock(Instant.parse("2026-10-05T09:00:00Z"))
    private val source = FakeUsageSource()
    private val store = FakeScreenTimeStore()
    private val rollups = FakeRollupStore()
    private val rollUp =
        RollUpEvents(clock, RecordingEventSink(), FakeEventLog(), rollups, FakeContentRepository(), store)
    private val ingest = IngestScreenTime(clock, source, store, rollUp)

    private val instagram = PackageName("com.instagram.android")
    private val notes = PackageName("com.samsung.android.app.notes")
    private val yesterday = LocalDate.parse("2026-10-04")
    private val today = LocalDate.parse("2026-10-05")

    init {
        source.apps[instagram] = AppInfo("Instagram", AppCategory.SOCIAL)
        source.apps[notes] = AppInfo("Samsung Notes", AppCategory.PRODUCTIVITY)
        source.apps[source.ownPackage] = AppInfo("Margin", AppCategory.OTHER)
    }

    private fun use(packageName: PackageName, from: String, until: String) {
        source.events += UsageEvent.Resumed(Instant.parse(from), packageName)
        source.events += UsageEvent.Paused(Instant.parse(until), packageName)
    }

    private fun minutesOn(date: LocalDate): Map<String, Long> =
        store.saved[date].orEmpty().associate { app -> app.label to app.foreground.inWholeMinutes }

    @Test
    fun `yesterday's foreground time is saved per app with its category`() = runTest {
        use(instagram, "2026-10-04T20:00:00Z", "2026-10-04T20:40:00Z")
        use(notes, "2026-10-04T21:00:00Z", "2026-10-04T21:10:00Z")

        assertEquals(ScreenTimeIngestion.DONE, ingest.missedDays())

        assertEquals(mapOf("Instagram" to 40L, "Samsung Notes" to 10L), minutesOn(yesterday))
        val doomApps = store.saved.getValue(yesterday).filter { it.isDoom }.map { it.label }
        assertEquals(listOf("Instagram"), doomApps)
        assertEquals(yesterday, store.ingestedThrough)
    }

    @Test
    fun `margin's own time is never doom whatever its category`() = runTest {
        source.apps[source.ownPackage] = AppInfo("Margin", AppCategory.SOCIAL)
        use(source.ownPackage, "2026-10-04T08:00:00Z", "2026-10-04T08:20:00Z")

        ingest.missedDays()

        val margin = store.saved.getValue(yesterday).single()
        assertEquals(AppCategory.MARGIN, margin.category)
        assertFalse(margin.isDoom)
    }

    @Test
    fun `an override decides whether an app is doom`() = runTest {
        store.overrides[instagram] = false
        store.overrides[notes] = true
        use(instagram, "2026-10-04T20:00:00Z", "2026-10-04T20:40:00Z")
        use(notes, "2026-10-04T21:00:00Z", "2026-10-04T21:10:00Z")

        ingest.missedDays()

        assertEquals(listOf("Samsung Notes"), store.saved.getValue(yesterday).filter { it.isDoom }.map { it.label })
    }

    @Test
    fun `the first run backfills a week and later runs only add new days`() = runTest {
        ingest.missedDays()

        assertEquals(LocalDate.parse("2026-09-28"), store.saved.keys.min())
        assertEquals(IngestScreenTime.BACKFILL_DAYS.toInt(), store.saved.size)

        clock.instant = Instant.parse("2026-10-06T00:30:00Z")
        source.queried.clear()
        ingest.missedDays()

        assertEquals(1, source.queried.size)
        assertEquals(today, store.ingestedThrough)
    }

    @Test
    fun `a locked phone stops ingestion so it can be retried`() = runTest {
        source.isLocked = true

        assertEquals(ScreenTimeIngestion.LOCKED, ingest.missedDays())

        assertTrue(store.saved.isEmpty())
        assertNull(store.ingestedThrough)
    }

    @Test
    fun `revoked access stops ingestion and keeps the saved days`() = runTest {
        use(instagram, "2026-10-03T20:00:00Z", "2026-10-03T20:40:00Z")
        clock.instant = Instant.parse("2026-10-04T09:00:00Z")
        ingest.missedDays()
        source.isGranted = false
        clock.instant = Instant.parse("2026-10-05T09:00:00Z")

        assertEquals(ScreenTimeIngestion.NO_ACCESS, ingest())

        assertEquals(LocalDate.parse("2026-10-03"), store.saved.keys.max())
        assertEquals(mapOf("Instagram" to 40L), minutesOn(LocalDate.parse("2026-10-03")))
    }

    @Test
    fun `today so far counts up to now`() = runTest {
        source.events += UsageEvent.Resumed(Instant.parse("2026-10-05T08:30:00Z"), instagram)

        assertEquals(ScreenTimeIngestion.DONE, ingest.today())

        assertEquals(mapOf("Instagram" to 30L), minutesOn(today))
    }

    @Test
    fun `ingested days fill the rollup's doom minutes and top doom apps`() = runTest {
        use(instagram, "2026-10-04T20:00:00Z", "2026-10-04T20:40:00Z")
        use(notes, "2026-10-04T21:00:00Z", "2026-10-04T21:10:00Z")

        ingest.missedDays()

        val screenTime = rollups.saved.getValue(yesterday).metrics.screenTime!!
        assertEquals(50.minutes, screenTime.screen)
        assertEquals(40.minutes, screenTime.doom)
        assertEquals(listOf("Instagram"), screenTime.topDoomApps)
    }

    @Test
    fun `deleted screen time is not ingested again`() = runTest {
        use(instagram, "2026-10-04T20:00:00Z", "2026-10-04T20:40:00Z")
        ingest.missedDays()

        DeleteScreenTime(clock, store, rollUp)()
        store.ingestedThrough = null
        ingest.missedDays()

        assertTrue(store.saved.isEmpty())
        assertNull(rollups.saved.getValue(yesterday).metrics.screenTime)
    }

    @Test
    fun `marking an app doom updates the days it was used`() = runTest {
        use(notes, "2026-10-04T21:00:00Z", "2026-10-04T21:10:00Z")
        ingest.missedDays()

        SetAppDoom(store, rollUp)(notes, isDoom = true)

        assertTrue(store.saved.getValue(yesterday).single().isDoom)
        assertEquals(10.minutes, rollups.saved.getValue(yesterday).metrics.screenTime!!.doom)
    }
}
