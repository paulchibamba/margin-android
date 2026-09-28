package com.paulchibamba.margin.domain.stats

import com.paulchibamba.margin.domain.actions.PostAction
import com.paulchibamba.margin.domain.feed.appSec
import com.paulchibamba.margin.domain.feed.cia
import com.paulchibamba.margin.domain.feed.grokking
import com.paulchibamba.margin.domain.memory.Rating
import com.paulchibamba.margin.domain.model.Format
import com.paulchibamba.margin.domain.model.NotePosition
import com.paulchibamba.margin.domain.usecase.BookFrontier
import com.paulchibamba.margin.domain.usecase.StatsReport
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

class StatsTextFormatterTest {

    private val busyReport = StatsReport(
        date = LocalDate.parse("2026-10-01"),
        postsSeen = 1284,
        currentStreak = 7,
        affinity = mapOf(Format.CHECKLIST to 0.3, Format.SPOT_BUG to 0.92, Format.DIALOGUE to 0.785),
        actionCounts = mapOf(PostAction.GOT to 12, PostAction.LOST to 1, PostAction.LESS to 3),
        lostConcepts = listOf(cia),
        reviewCounts = mapOf(Rating.AGAIN to 3, Rating.HARD to 4, Rating.GOOD to 18),
        frontiers = listOf(BookFrontier(appSec, NotePosition(3, 1)), BookFrontier(grokking, null)),
    )

    private val emptyReport = StatsReport(
        date = LocalDate.parse("2026-10-01"),
        postsSeen = 0,
        currentStreak = 0,
        affinity = emptyMap(),
        actionCounts = emptyMap(),
        lostConcepts = emptyList(),
        reviewCounts = emptyMap(),
        frontiers = listOf(BookFrontier(appSec, null)),
    )

    @Test
    fun `a busy report exports every section in a stable order`() {
        val expected = """
            |Margin stats 2026-10-01
            |Posts seen: 1284
            |Day streak: 7
            |Marked lost: 1
            |Review accuracy: 88% of 25 reviews
            |
            |Format affinity
            |  spot_bug 0.92
            |  dialogue 0.79
            |  checklist 0.30
            |
            |Reviews by grade
            |  again 3 (12%)
            |  hard 4 (16%)
            |  good 18 (72%)
            |
            |Frontier per book
            |  alice-bob-appsec: ch 3 note 2
            |  grokking-web-app-security: not started
            |
            |Actions by type
            |  got 12
            |  lost 1
            |  read 0
            |  save 0
            |  less 3
            |
            |Lost concepts
            |  alice-bob-appsec/ch1/c0 Concept alice-bob-appsec 1.0
            |""".trimMargin()

        assertEquals(expected, StatsTextFormatter.format(busyReport))
    }

    @Test
    fun `an empty report says none instead of leaving sections blank`() {
        val expected = """
            |Margin stats 2026-10-01
            |Posts seen: 0
            |Day streak: 0
            |Marked lost: 0
            |Review accuracy: no reviews yet
            |
            |Format affinity
            |  none
            |
            |Reviews by grade
            |  again 0 (0%)
            |  hard 0 (0%)
            |  good 0 (0%)
            |
            |Frontier per book
            |  alice-bob-appsec: not started
            |
            |Actions by type
            |  got 0
            |  lost 0
            |  read 0
            |  save 0
            |  less 0
            |
            |Lost concepts
            |  none
            |""".trimMargin()

        assertEquals(expected, StatsTextFormatter.format(emptyReport))
    }
}
