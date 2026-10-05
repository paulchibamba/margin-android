package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.feed.FeedState
import com.paulchibamba.margin.domain.feed.cia
import com.paulchibamba.margin.domain.feed.introducedProgress
import com.paulchibamba.margin.domain.feed.tipOf
import com.paulchibamba.margin.domain.model.NoteId
import com.paulchibamba.margin.domain.progress.Introduction
import com.paulchibamba.margin.domain.progress.PastReward
import com.paulchibamba.margin.domain.progress.RewardHistory
import com.paulchibamba.margin.domain.progress.RewardKind
import com.paulchibamba.margin.domain.tracking.Event
import com.paulchibamba.margin.domain.tracking.FakeEventLog
import com.paulchibamba.margin.domain.tracking.LoggedEvent
import com.paulchibamba.margin.domain.tracking.NoteOpenVia
import kotlinx.coroutines.test.runTest
import java.time.Instant
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BuildProgressFactsTest {
    private val clock = FixedClock(Instant.parse("2026-10-05T12:00:00Z"))
    private val progress = FakeProgressRepository()
    private val eventLog = FakeEventLog()
    private val buildFacts = BuildProgressFacts(
        FakeContentRepository(),
        progress,
        FakeSettingsRepository(),
        eventLog,
        clock,
    )

    @Test
    fun `facts are built from the stored progress, first-seen times and recent events`() = runTest {
        progress.feedState.value = FeedState(rewardAtStep = 9).withProgress(cia.id, introducedProgress())
        progress.firstSeen[tipOf(cia).id] = Instant.parse("2026-10-03T09:00:00Z")
        val reopen = Event.NoteOpen(cia.sourceNoteId!!, NoteOpenVia.CHAPTER, openCount = 2)
        eventLog.logged += LoggedEvent(Instant.parse("2026-10-05T11:00:00Z"), null, reopen)
        eventLog.logged += LoggedEvent(Instant.parse("2026-09-01T11:00:00Z"), null, reopen)

        val facts = buildFacts()

        assertEquals(LocalDate.of(2026, 10, 5), facts.today)
        assertEquals(listOf(Introduction(cia, LocalDate.of(2026, 10, 3))), facts.introductions)
        assertEquals(listOf(cia), facts.struggles.map { it.concept })
        assertTrue(facts.attention.hotspots.isEmpty())
    }

    @Test
    fun `the reward history decides which read notes are still quotable`() = runTest {
        val quoted = cia.sourceNoteId!!
        progress.markNoteRead(quoted, clock.instant)
        progress.markNoteRead(NoteId("${quoted.value}x"), clock.instant)
        val quote = PastReward(RewardKind.Quote, emptyList(), quoted, emptyMap(), title = "", clock.instant)
        val history = RewardHistory(listOf(quote))

        assertTrue(buildFacts(history).unquotedReadNotes.none { it.note.id == quoted })
        assertEquals(listOf(quoted), buildFacts().unquotedReadNotes.map { it.note.id })
    }
}
