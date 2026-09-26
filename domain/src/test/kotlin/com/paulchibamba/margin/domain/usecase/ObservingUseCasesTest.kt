package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.actions.PostAction
import com.paulchibamba.margin.domain.feed.Confidence
import com.paulchibamba.margin.domain.feed.appSec
import com.paulchibamba.margin.domain.feed.cia
import com.paulchibamba.margin.domain.feed.defenceInDepth
import com.paulchibamba.margin.domain.feed.grokking
import com.paulchibamba.margin.domain.feed.introducedProgress
import com.paulchibamba.margin.domain.feed.learnedCard
import com.paulchibamba.margin.domain.feed.leastPrivilege
import com.paulchibamba.margin.domain.feed.noteId
import com.paulchibamba.margin.domain.feed.tipOf
import com.paulchibamba.margin.domain.feed.withIntroduced
import com.paulchibamba.margin.domain.memory.CardState
import com.paulchibamba.margin.domain.model.ChapterRef
import com.paulchibamba.margin.domain.model.NotePosition
import com.paulchibamba.margin.domain.rewards.Badge
import com.paulchibamba.margin.domain.rewards.BadgeKind
import com.paulchibamba.margin.domain.rewards.StreakDayStatus
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.minutes

class ObservingUseCasesTest {

    private val fixture = UseCaseFixture()

    @Test
    fun `the reading home tallies each book and continues after the last note read`() = runTest {
        fixture.markNoteRead(noteId(appSec, 1, 0))

        val home = fixture.observeReadingHome().first()

        assertEquals(noteId(appSec, 1, 1), home.continueNote?.id)
        val appSecTally = home.books.first { it.book == appSec }.tally
        assertEquals(NoteTally(notesRead = 1, noteCount = 4, timeLeft = 3.minutes), appSecTally)
    }

    @Test
    fun `a book lists chapters with reading-only, known and the next note to open`() = runTest {
        fixture.markNoteRead(noteId(appSec, 1, 0))
        fixture.markChapterKnown(ChapterRef(appSec.slug, 3))

        val chapters = fixture.observeBook(appSec.slug).first().chapters.associateBy { it.chapter.number }

        assertEquals(noteId(appSec, 1, 1), chapters.getValue(1).nextNote)
        assertTrue(chapters.getValue(2).chapter.isReadingOnly)
        assertTrue(chapters.getValue(3).isDone)
        assertFalse(chapters.getValue(1).isDone)
    }

    @Test
    fun `the streak summary shows today's progress in the week`() = runTest {
        fixture.markNoteRead(noteId(appSec, 1, 0))

        val streak = fixture.observeStreak().first()

        assertEquals(1, streak.currentStreak)
        assertEquals(StreakDayStatus.DONE, streak.week.single { it.date.toString() == "2026-10-01" }.status)
    }

    @Test
    fun `stats count actions, lost concepts and frontiers`() = runTest {
        fixture.getNextPost()
        fixture.applyPostAction(tipOf(cia), PostAction.LOST)
        fixture.markNoteRead(noteId(grokking, 1, 0))

        val stats = fixture.observeStats().first()

        assertEquals(mapOf(PostAction.LOST to 1), stats.actionCounts)
        assertEquals(listOf(cia), stats.lostConcepts)
        assertEquals(NotePosition(1, 0), stats.frontiers[grokking.slug])
        assertEquals(null, stats.frontiers[appSec.slug])
        assertEquals(1, stats.postsSeen)
    }

    @Test
    fun `book settings keep between one and three active books`() = runTest {
        val onlyActive = fixture.settings.bookSettings.value.map { it.copy(isActive = it.bookSlug == appSec.slug) }
        fixture.settings.bookSettings.value = onlyActive

        val deactivatingLast = fixture.updateBookSettings(onlyActive.first().copy(isActive = false))
        val activatingAnother = fixture.updateBookSettings(onlyActive.last().copy(isActive = true))

        assertFalse(deactivatingLast)
        assertTrue(activatingAnother)
        assertEquals(2, fixture.settings.bookSettings.value.count { it.isActive })
    }

    @Test
    fun `a new badge is reported once`() = runTest {
        val introduced = listOf(cia, leastPrivilege, defenceInDepth)
        fixture.progress.feedState.value = fixture.engines.feedEngine(0.9).startingState()
            .withIntroduced(*introduced.toTypedArray(), progress = introducedProgress())

        assertEquals(listOf(Badge(appSec.slug, BadgeKind.INTRODUCED)), fixture.consumeNewBadges())
        assertEquals(emptyList(), fixture.consumeNewBadges())
    }

    @Test
    fun `a fully remembered book earns the Remembered badge too`() = runTest {
        val mature = introducedProgress().copy(card = learnedCard().copy(state = CardState.REVIEW, scheduledDays = 30))
        fixture.progress.feedState.value = fixture.engines.feedEngine(0.9).startingState()
            .withIntroduced(cia, leastPrivilege, defenceInDepth, progress = mature.copy(confidence = Confidence.GOT))

        val kinds = fixture.consumeNewBadges().map { it.kind }.toSet()
        assertEquals(setOf(BadgeKind.INTRODUCED, BadgeKind.REMEMBERED), kinds)
    }
}
