package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.actions.PostAction
import com.paulchibamba.margin.domain.feed.Confidence
import com.paulchibamba.margin.domain.feed.aiSecurity
import com.paulchibamba.margin.domain.feed.appSec
import com.paulchibamba.margin.domain.feed.cia
import com.paulchibamba.margin.domain.feed.defenceInDepth
import com.paulchibamba.margin.domain.feed.freshState
import com.paulchibamba.margin.domain.feed.grokking
import com.paulchibamba.margin.domain.feed.introducedProgress
import com.paulchibamba.margin.domain.feed.learnedCard
import com.paulchibamba.margin.domain.feed.leastPrivilege
import com.paulchibamba.margin.domain.feed.noteId
import com.paulchibamba.margin.domain.feed.postsOf
import com.paulchibamba.margin.domain.feed.readingOnlyIntro
import com.paulchibamba.margin.domain.feed.tipOf
import com.paulchibamba.margin.domain.feed.withIntroduced
import com.paulchibamba.margin.domain.memory.CardState
import com.paulchibamba.margin.domain.model.BookSettings
import com.paulchibamba.margin.domain.model.ChapterRef
import com.paulchibamba.margin.domain.model.NotePosition
import com.paulchibamba.margin.domain.model.Priority
import com.paulchibamba.margin.domain.progression.ReadingState
import com.paulchibamba.margin.domain.rewards.Badge
import com.paulchibamba.margin.domain.rewards.BadgeKind
import com.paulchibamba.margin.domain.rewards.StreakDayStatus
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.minutes

class ObservingUseCasesTest {

    private val fixture = UseCaseFixture()

    @Test
    fun `the reading home tallies each book and continues after the last note read`() = runTest {
        fixture.markNoteRead(noteId(appSec, 1, 0))

        val home = fixture.observeReadingHome().first()

        assertEquals(noteId(appSec, 1, 1), home.continueNote?.outline?.id)
        val appSecTally = home.books.first { it.book == appSec }.tally
        assertEquals(NoteTally(notesRead = 1, noteCount = 4, timeLeft = 3.minutes), appSecTally)
    }

    @Test
    fun `the reading home continues at the last note when it is still unread`() = runTest {
        fixture.progress.reading.value = ReadingState(lastNote = noteId(grokking, 1, 0))

        assertEquals(noteId(grokking, 1, 0), fixture.observeReadingHome().first().continueNote?.outline?.id)
    }

    @Test
    fun `with no last note the reading home continues at the main book's first unread note`() = runTest {
        fixture.progress.reading.value = ReadingState(knownChapters = setOf(ChapterRef(appSec.slug, 1)))

        assertEquals(noteId(appSec, 2, 0), fixture.observeReadingHome().first().continueNote?.outline?.id)
    }

    @Test
    fun `the continue note knows its place in the chapter and the posts it unlocks`() = runTest {
        fixture.markNoteRead(noteId(appSec, 1, 0))

        val continueNote = fixture.observeReadingHome().first().continueNote

        assertEquals(PlaceInChapter(order = 2, noteCount = 2), continueNote?.place)
        assertEquals(postsOf(leastPrivilege).size, continueNote?.unlockedPosts)
        assertEquals("Chapter 1", continueNote?.chapterTitle)
    }

    @Test
    fun `the reading home lists active books by priority before inactive ones`() = runTest {
        fixture.settings.bookSettings.value = listOf(
            BookSettings(appSec.slug, isActive = true, priority = Priority.NORMAL),
            BookSettings(grokking.slug, isActive = true, priority = Priority.MAIN),
            BookSettings(aiSecurity.slug, isActive = false, priority = Priority.LOW),
        )

        val books = fixture.observeReadingHome().first().books

        assertEquals(listOf(grokking, appSec, aiSecurity), books.map { it.book })
        assertEquals(listOf(true, true, false), books.map { it.isActive })
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
    fun `marking a chapter known moves the frontier to its end and undo restores it`() = runTest {
        fixture.markNoteRead(noteId(appSec, 1, 0))
        val chapterThree = ChapterRef(appSec.slug, 3)

        fixture.markChapterKnown(chapterThree)
        val isUnlockedWhenKnown = fixture.libraryLoader.load().isUnlocked(defenceInDepth)
        fixture.unmarkChapterKnown(chapterThree)
        val isUnlockedAfterUndo = fixture.libraryLoader.load().isUnlocked(defenceInDepth)

        assertTrue(isUnlockedWhenKnown)
        assertFalse(isUnlockedAfterUndo)
        assertTrue(fixture.libraryLoader.load().isUnlocked(cia))
    }

    @Test
    fun `a book's introduced count leaves out reading-only chapters`() = runTest {
        fixture.progress.feedState.value = freshState.withIntroduced(cia, readingOnlyIntro)

        val completion = fixture.observeBook(appSec.slug).first().completion

        assertEquals(1, completion.introduced)
        assertEquals(3, completion.total)
    }

    @Test
    fun `a note knows its place in the chapter and its neighbours across chapters`() = runTest {
        val reading = fixture.observeNote(noteId(appSec, 1, 1)).first()

        assertEquals(PlaceInChapter(order = 2, noteCount = 2), reading.place)
        assertEquals(noteId(appSec, 1, 0), reading.previous)
        assertEquals(noteId(appSec, 2, 0), reading.next)
        assertEquals("Chapter 1", reading.chapterTitle)
    }

    @Test
    fun `a note's read state follows the reading progress`() = runTest {
        val note = noteId(appSec, 1, 0)

        fixture.markNoteRead(note)

        assertTrue(fixture.observeNote(note).first().isRead)
    }

    @Test
    fun `the first and last notes of a book have no neighbour beyond the book`() = runTest {
        assertNull(fixture.observeNote(noteId(appSec, 1, 0)).first().previous)
        assertNull(fixture.observeNote(noteId(appSec, 3, 0)).first().next)
    }

    @Test
    fun `the reading home continues at the note last opened`() = runTest {
        fixture.markNoteRead(noteId(appSec, 1, 0))
        fixture.rememberLastNote(noteId(appSec, 2, 0))

        assertEquals(noteId(appSec, 2, 0), fixture.observeReadingHome().first().continueNote?.outline?.id)
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
