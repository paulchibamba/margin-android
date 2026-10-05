package com.paulchibamba.margin.domain.rollup

import com.paulchibamba.margin.domain.model.BookSlug
import com.paulchibamba.margin.domain.model.Format
import com.paulchibamba.margin.domain.rollup.RollupFixture.leastPrivilege
import com.paulchibamba.margin.domain.rollup.RollupFixture.readNote
import java.time.ZoneOffset
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.Duration.Companion.seconds

class AttentionSummaryTest {
    private val fixtureDay = RollupMetrics.of(RollupFixture.day)

    @Test
    fun `rates are weighted by each day's posts`() {
        val quietDay = fixtureDay.copy(posts = PostMetrics(4, exposures = 4, glances = 3, deepReads = 0, emptyMap()))

        val summary = AttentionSummary.of(listOf(fixtureDay, quietDay))

        assertEquals(0.5, summary.glanceRate)
        assertEquals(0.25, summary.deepRate)
    }

    @Test
    fun `the ratio per format is weighted by exposures`() {
        val factDay = PostMetrics(1, 1, 0, 1, mapOf(Format.FACT to FormatAttention(1.5, 1)))

        val summary = AttentionSummary.of(listOf(fixtureDay, fixtureDay.copy(posts = factDay)))

        assertEquals(mapOf(Format.MCQ to 1.0, Format.FACT to 0.75), summary.ratioByFormat)
    }

    @Test
    fun `hotspots and reading pace add up across days`() {
        val summary = AttentionSummary.of(listOf(fixtureDay, fixtureDay))

        assertEquals(RereadHotspot(RereadSubject.OfNote(readNote), 6), summary.hotspots.first())
        assertEquals(RereadHotspot(RereadSubject.OfConcept(leastPrivilege), 2), summary.hotspots[1])
        assertEquals(mapOf(BookSlug("appsec") to 200, BookSlug("grokking") to 300), summary.paceByBook)
    }

    @Test
    fun `pace over a whole week is total words over total time`() {
        val slowDay = fixtureDay.copy(
            reading = ReadingMetrics(1, mapOf(BookSlug("appsec") to ReadingPace(100, 60.seconds)), emptyMap()),
        )

        val summary = AttentionSummary.of(listOf(fixtureDay, slowDay))

        assertEquals(150, summary.paceByBook[BookSlug("appsec")])
    }

    @Test
    fun `no rollups give an empty summary`() {
        val summary = AttentionSummary.of(listOf(RollupMetrics.of(DayEvents(emptyList(), ZoneOffset.UTC))))

        assertNull(summary.glanceRate)
        assertEquals(emptyMap(), summary.ratioByFormat)
        assertEquals(emptyList(), summary.hotspots)
    }
}
