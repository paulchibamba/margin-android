package com.paulchibamba.margin.feature.stats

import com.paulchibamba.margin.domain.actions.PostAction
import com.paulchibamba.margin.domain.memory.Rating
import com.paulchibamba.margin.domain.model.Book
import com.paulchibamba.margin.domain.model.BookSlug
import com.paulchibamba.margin.domain.model.Concept
import com.paulchibamba.margin.domain.model.ConceptId
import com.paulchibamba.margin.domain.model.Format
import com.paulchibamba.margin.domain.model.NotePosition
import com.paulchibamba.margin.domain.rollup.TimeOfDay
import com.paulchibamba.margin.domain.usecase.AttentionReport
import com.paulchibamba.margin.domain.usecase.BookFrontier
import com.paulchibamba.margin.domain.usecase.ScreenTimeDay
import com.paulchibamba.margin.domain.usecase.ScreenTimeReport
import com.paulchibamba.margin.domain.usecase.StatsReport
import java.time.LocalDate
import kotlin.time.Duration.Companion.minutes

object StatsPreviewData {
    private val appSec = Book(BookSlug("alice-bob-appsec"), "Alice & Bob Learn AppSec")
    private val tangledWeb = Book(BookSlug("tangled-web"), "The Tangled Web")

    val busy = StatsReport(
        date = LocalDate.parse("2026-10-01"),
        postsSeen = 1284,
        currentStreak = 7,
        affinity = mapOf(Format.SPOT_BUG to 0.92, Format.DIALOGUE to 0.78, Format.MCQ to 0.64, Format.CHECKLIST to 0.3),
        actionCounts = mapOf(PostAction.GOT to 212, PostAction.LOST to 9, PostAction.READ to 31, PostAction.SAVE to 14),
        lostConcepts = List(9) { index -> lostConcept(index) },
        reviewCounts = mapOf(Rating.AGAIN to 41, Rating.HARD to 62, Rating.GOOD to 240),
        frontiers = listOf(BookFrontier(appSec, NotePosition(4, 14)), BookFrontier(tangledWeb, null)),
    )

    val empty = StatsReport(
        date = LocalDate.parse("2026-10-01"),
        postsSeen = 0,
        currentStreak = 0,
        affinity = emptyMap(),
        actionCounts = emptyMap(),
        lostConcepts = emptyList(),
        reviewCounts = emptyMap(),
        frontiers = listOf(BookFrontier(appSec, null), BookFrontier(tangledWeb, null)),
    )

    val busyAttention = AttentionReport(
        glanceRate = 0.18,
        deepRate = 0.42,
        ratioByFormat = listOf(Format.SPOT_BUG to 1.12, Format.DIALOGUE to 0.84, Format.FACT to 0.41),
        hotspots = listOf("Least privilege" to 4, "Ch 1 · Confidentiality" to 3),
        paceByBook = listOf(appSec to 212, tangledWeb to 188),
        paceByTimeOfDay = listOf(TimeOfDay.MORNING to 230, TimeOfDay.EVENING to 175),
    )

    val emptyAttention = AttentionReport(
        glanceRate = null,
        deepRate = null,
        ratioByFormat = emptyList(),
        hotspots = emptyList(),
        paceByBook = emptyList(),
        paceByTimeOfDay = emptyList(),
    )

    val screenTime = ScreenTimeReport(
        (0L..6L).map { day ->
            ScreenTimeDay(LocalDate.of(2026, 9, 29).plusDays(day), (day * 4 + 3).minutes, (95 - day * 9).minutes)
        },
    )

    private fun lostConcept(index: Int) = Concept(
        id = ConceptId("alice-bob-appsec/ch1/c$index"),
        bookSlug = appSec.slug,
        chapter = 1,
        order = index,
        title = "Concept $index",
        summary = "",
        section = "",
        sourceNoteId = null,
    )
}
