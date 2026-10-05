package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.feed.appSec
import com.paulchibamba.margin.domain.feed.cia
import com.paulchibamba.margin.domain.feed.grokking
import com.paulchibamba.margin.domain.feed.leastPrivilege
import com.paulchibamba.margin.domain.model.Format
import com.paulchibamba.margin.domain.model.NoteId
import com.paulchibamba.margin.domain.rollup.DailyRollup
import com.paulchibamba.margin.domain.rollup.DayEvents
import com.paulchibamba.margin.domain.rollup.FakeRollupStore
import com.paulchibamba.margin.domain.rollup.FormatAttention
import com.paulchibamba.margin.domain.rollup.ReadingMetrics
import com.paulchibamba.margin.domain.rollup.ReadingPace
import com.paulchibamba.margin.domain.rollup.RereadHotspot
import com.paulchibamba.margin.domain.rollup.RereadMetrics
import com.paulchibamba.margin.domain.rollup.RereadSubject
import com.paulchibamba.margin.domain.rollup.RollupMetrics
import com.paulchibamba.margin.domain.rollup.TimeOfDay
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.minutes
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest

class ObserveAttentionTest {
    private val clock = FixedClock(Instant.parse("2026-10-07T09:00:00Z"))
    private val store = FakeRollupStore()
    private val observe = ObserveAttention(store, FakeContentRepository(), clock)
    private val emptyDay = RollupMetrics.of(DayEvents(emptyList(), ZoneOffset.UTC))

    private suspend fun saveDay(date: String, metrics: RollupMetrics) =
        store.save(DailyRollup(LocalDate.parse(date), metrics, clock.instant))

    @Test
    fun `only the last seven days count`() = runTest {
        saveDay("2026-09-30", dayWith(Format.FACT to ratio(0.2)))
        saveDay("2026-10-01", dayWith(Format.MCQ to ratio(1.0)))

        assertEquals(listOf(Format.MCQ to 1.0), observe().first().ratioByFormat)
    }

    @Test
    fun `hotspots show concept and note titles`() = runTest {
        val note = cia.sourceNoteId!!
        val hotspots = listOf(
            RereadHotspot(RereadSubject.OfConcept(leastPrivilege.id), 3),
            RereadHotspot(RereadSubject.OfNote(note), 2),
            RereadHotspot(RereadSubject.OfNote(NoteId("gone/ch1/n001")), 1),
        )
        saveDay("2026-10-07", emptyDay.copy(rereads = RereadMetrics(0, 0, 0, hotspots)))

        val expected = listOf(leastPrivilege.title to 3, "Ch 1 · Section of ${cia.title}" to 2)
        assertEquals(expected, observe().first().hotspots)
    }

    @Test
    fun `pace follows the library order of books and the order of the day`() = runTest {
        val pace = ReadingPace(200, 1.minutes)
        val reading = ReadingMetrics(
            notesRead = 2,
            paceByBook = mapOf(grokking.slug to pace, appSec.slug to pace),
            paceByTimeOfDay = mapOf(TimeOfDay.NIGHT to pace, TimeOfDay.MORNING to pace),
        )
        saveDay("2026-10-07", emptyDay.copy(reading = reading))

        val attention = observe().first()

        assertEquals(listOf(appSec to 200, grokking to 200), attention.paceByBook)
        assertEquals(listOf(TimeOfDay.MORNING to 200, TimeOfDay.NIGHT to 200), attention.paceByTimeOfDay)
    }

    private fun dayWith(vararg ratios: Pair<Format, FormatAttention>) =
        emptyDay.copy(posts = emptyDay.posts.copy(ratioByFormat = mapOf(*ratios)))

    private fun ratio(median: Double) = FormatAttention(median, exposures = 1)
}
