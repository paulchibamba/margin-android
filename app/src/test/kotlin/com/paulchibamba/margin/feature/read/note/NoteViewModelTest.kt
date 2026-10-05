package com.paulchibamba.margin.feature.read.note

import androidx.lifecycle.SavedStateHandle
import com.paulchibamba.margin.domain.feed.appSec
import com.paulchibamba.margin.domain.feed.noteId
import com.paulchibamba.margin.domain.model.Note
import com.paulchibamba.margin.domain.model.NoteId
import com.paulchibamba.margin.domain.model.NotePosition
import com.paulchibamba.margin.domain.model.PostId
import com.paulchibamba.margin.domain.tracking.Event
import com.paulchibamba.margin.domain.tracking.FakeEventLog
import com.paulchibamba.margin.domain.tracking.NoteAttention
import com.paulchibamba.margin.domain.tracking.NoteOpenVia
import com.paulchibamba.margin.domain.tracking.RecordingEventSink
import com.paulchibamba.margin.domain.usecase.ConsumeNewBadges
import com.paulchibamba.margin.domain.usecase.FakeContentRepository
import com.paulchibamba.margin.domain.usecase.FakeProgressRepository
import com.paulchibamba.margin.domain.usecase.FakeSettingsRepository
import com.paulchibamba.margin.domain.usecase.FeedStateLock
import com.paulchibamba.margin.domain.usecase.MarkNoteRead
import com.paulchibamba.margin.domain.usecase.ObserveNote
import com.paulchibamba.margin.domain.usecase.ObserveReadingHome
import com.paulchibamba.margin.domain.usecase.RememberLastNote
import com.paulchibamba.margin.domain.usecase.UnlockedPostCount
import com.paulchibamba.margin.feature.celebration.Celebration
import com.paulchibamba.margin.feature.celebration.CelebrationQueue
import com.paulchibamba.margin.feature.celebration.CelebrationTrigger
import com.paulchibamba.margin.feature.feed.FakeClock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class NoteViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val clock = FakeClock()
    private val notes = (1..7).map { order -> noteOf(order, wordCount = 200) } + noteOf(8, wordCount = 30)
    private val content = FakeContentRepository(notes = notes)
    private val progress = FakeProgressRepository()
    private val celebrations = CelebrationQueue()
    private val recorded = mutableListOf<Event>()
    private val attention = NoteAttention(clock, recorded::add, RecordingEventSink(), FakeEventLog())

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `moving on after eight seconds marks the note read`() = runTest(dispatcher) {
        val viewModel = noteViewModel(noteId(appSec, 1, 1))
        runCurrent()

        wait(8.seconds)
        viewModel.onNext()
        runCurrent()

        assertEquals(setOf(noteId(appSec, 1, 1)), progress.reading.value.readNotes)
        assertEquals(noteId(appSec, 1, 2), viewModel.uiState.value.note)
    }

    @Test
    fun `the day's first note read queues the streak celebration once`() = runTest(dispatcher) {
        val viewModel = noteViewModel(noteId(appSec, 1, 1))
        runCurrent()

        wait(8.seconds)
        viewModel.onNext()
        runCurrent()
        wait(8.seconds)
        viewModel.onNext()
        runCurrent()

        assertEquals(2, progress.reading.value.readNotes.size)
        assertEquals(listOf<Celebration>(Celebration.StreakExtended), celebrations.pending.value)
    }

    @Test
    fun `moving on too soon leaves the note unread`() = runTest(dispatcher) {
        val viewModel = noteViewModel(noteId(appSec, 1, 1))
        runCurrent()

        wait(7.seconds)
        viewModel.onNext()
        runCurrent()

        assertEquals(emptySet(), progress.reading.value.readNotes)
        assertEquals(noteId(appSec, 1, 2), viewModel.uiState.value.note)
    }

    @Test
    fun `the read mark appears once the eight seconds pass`() = runTest(dispatcher) {
        val viewModel = noteViewModel(noteId(appSec, 1, 1))
        runCurrent()

        wait(7.seconds)
        val isReadEarly = viewModel.uiState.value.isRead
        wait(1.seconds)

        assertFalse(isReadEarly)
        assertTrue(viewModel.uiState.value.isRead)
    }

    @Test
    fun `a note under sixty words is read at once`() = runTest(dispatcher) {
        val viewModel = noteViewModel(noteId(appSec, 1, 8))
        runCurrent()

        assertTrue(viewModel.uiState.value.isRead)
        viewModel.onNext()
        runCurrent()
        assertEquals(setOf(noteId(appSec, 1, 8)), progress.reading.value.readNotes)
    }

    @Test
    fun `reading five notes then leaving continues at the sixth`() = runTest(dispatcher) {
        val viewModel = noteViewModel(noteId(appSec, 1, 1))
        runCurrent()

        repeat(5) {
            wait(10.seconds)
            viewModel.onNext()
            runCurrent()
        }
        val home = ObserveReadingHome(content, progress, FakeSettingsRepository(), unlockedPosts()).invoke().first()

        assertEquals(5, progress.reading.value.readNotes.size)
        assertEquals(noteId(appSec, 1, 6), home.continueNote?.outline?.id)
    }

    @Test
    fun `going back a note never marks it read`() = runTest(dispatcher) {
        val viewModel = noteViewModel(noteId(appSec, 1, 2))
        runCurrent()

        wait(10.seconds)
        viewModel.onPrevious()
        runCurrent()

        assertEquals(emptySet(), progress.reading.value.readNotes)
        assertEquals(noteId(appSec, 1, 1), viewModel.uiState.value.note)
    }

    @Test
    fun `a note opened from a post offers the way back to that post`() = runTest(dispatcher) {
        val viewModel = noteViewModel(noteId(appSec, 1, 1), fromPost = "cia-tip")
        runCurrent()

        assertEquals(PostId("cia-tip"), viewModel.uiState.value.fromPost)
        assertEquals("1/8", viewModel.uiState.value.positionLabel)
    }

    @Test
    fun `opening a note remembers it for continue`() = runTest(dispatcher) {
        noteViewModel(noteId(appSec, 1, 3))
        runCurrent()

        assertEquals(noteId(appSec, 1, 3), progress.reading.value.lastNote)
    }

    @Test
    fun `peeking at a note from a post leaves continue where it was`() = runTest(dispatcher) {
        noteViewModel(noteId(appSec, 1, 3), fromPost = "cia-tip")
        runCurrent()

        assertNull(progress.reading.value.lastNote)
    }

    @Test
    fun `opening a note records how it was opened, and moving on records the visit and the next open`() =
        runTest(dispatcher) {
            val viewModel = noteViewModel(noteId(appSec, 1, 1), via = NoteOpenVia.CONTINUE)
            runCurrent()

            wait(8.seconds)
            viewModel.onNext()
            runCurrent()

            val opens = recorded.filterIsInstance<Event.NoteOpen>()
            assertEquals(listOf(NoteOpenVia.CONTINUE, NoteOpenVia.NEXT), opens.map { it.via })
            val exposure = recorded.filterIsInstance<Event.NoteExposure>().single()
            assertEquals(noteId(appSec, 1, 1), exposure.noteId)
            assertTrue(exposure.isMarkedRead)
        }

    @Test
    fun `going back records the visit as not marked read and the earlier note as opened from previous`() =
        runTest(dispatcher) {
            val viewModel = noteViewModel(noteId(appSec, 1, 2))
            runCurrent()

            viewModel.onPrevious()
            runCurrent()

            assertFalse(recorded.filterIsInstance<Event.NoteExposure>().single().isMarkedRead)
            assertEquals(NoteOpenVia.PREVIOUS, recorded.filterIsInstance<Event.NoteOpen>().last().via)
        }

    private fun TestScope.wait(duration: Duration) {
        clock.advanceBy(duration)
        advanceTimeBy(duration)
        runCurrent()
    }

    private fun noteViewModel(note: NoteId, fromPost: String? = null, via: NoteOpenVia = NoteOpenVia.CHAPTER) =
        NoteViewModel(
            SavedStateHandle(mapOf("noteId" to note.value, "fromPost" to fromPost, "via" to via.name)),
            ObserveNote(content, progress),
            MarkNoteRead(progress, clock),
            RememberLastNote(progress),
            CelebrationTrigger(celebrations, consumeNewBadges()),
            clock,
            attention,
        )

    private fun consumeNewBadges() = ConsumeNewBadges(content, progress, FakeSettingsRepository(), FeedStateLock())

    private fun unlockedPosts() = UnlockedPostCount(content, FakeSettingsRepository())

    private fun noteOf(order: Int, wordCount: Int) = Note(
        id = noteId(appSec, 1, order),
        bookSlug = appSec.slug,
        position = NotePosition(chapter = 1, order = order),
        section = "Section $order",
        part = 1,
        partCount = 1,
        html = "<p>The book's own words.</p>",
        wordCount = wordCount,
        readingTime = 1.minutes,
    )
}
