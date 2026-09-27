package com.paulchibamba.margin.feature.read.home

import com.paulchibamba.margin.domain.feed.aiSecurity
import com.paulchibamba.margin.domain.feed.appSec
import com.paulchibamba.margin.domain.feed.grokking
import com.paulchibamba.margin.domain.feed.noteId
import com.paulchibamba.margin.domain.model.ChapterRef
import com.paulchibamba.margin.domain.progression.ReadingState
import com.paulchibamba.margin.domain.usecase.FakeContentRepository
import com.paulchibamba.margin.domain.usecase.FakeProgressRepository
import com.paulchibamba.margin.domain.usecase.FakeSettingsRepository
import com.paulchibamba.margin.domain.usecase.FixedClock
import com.paulchibamba.margin.domain.usecase.ObserveReadingHome
import com.paulchibamba.margin.domain.usecase.ObserveStreak
import com.paulchibamba.margin.domain.usecase.UnlockedPostCount
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class ReadHomeViewModelTest {

    private val content = FakeContentRepository()
    private val progress = FakeProgressRepository()
    private val settings = FakeSettingsRepository()

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `continue points at the note after the last note read`() = runTest {
        progress.reading.value = ReadingState(readNotes = setOf(noteId(appSec, 1, 0)), lastNote = noteId(appSec, 1, 0))

        assertEquals(noteId(appSec, 1, 1), loadedState().continueNote?.outline?.id)
    }

    @Test
    fun `continue points at the last note when it is not read yet`() = runTest {
        progress.reading.value = ReadingState(lastNote = noteId(grokking, 1, 0))

        assertEquals(noteId(grokking, 1, 0), loadedState().continueNote?.outline?.id)
    }

    @Test
    fun `with no last note continue points at the first unread note of the main book`() = runTest {
        progress.reading.value = ReadingState(readNotes = setOf(noteId(appSec, 1, 0)))

        assertEquals(noteId(appSec, 1, 1), loadedState().continueNote?.outline?.id)
    }

    @Test
    fun `with no last note continue skips chapters marked as known`() = runTest {
        progress.reading.value = ReadingState(knownChapters = setOf(ChapterRef(appSec.slug, 1)))

        assertEquals(noteId(appSec, 2, 0), loadedState().continueNote?.outline?.id)
    }

    @Test
    fun `with every note read there is nothing to continue`() = runTest {
        val allNotes = content.noteOutlines().map { it.id }.toSet()
        progress.reading.value = ReadingState(readNotes = allNotes, lastNote = allNotes.last())

        assertNull(loadedState().continueNote)
    }

    @Test
    fun `inactive books are listed last with no priority tag`() = runTest {
        val state = loadedState()

        assertEquals(listOf(appSec.slug, grokking.slug, aiSecurity.slug), state.rings.map { it.book })
        assertEquals(listOf(true, true, false), state.rings.map { it.isActive })
        assertNull(state.library.last().priority)
    }

    @Test
    fun `ring progress is the share of notes read`() = runTest {
        progress.reading.value = ReadingState(readNotes = setOf(noteId(appSec, 1, 0)))

        assertEquals(0.25f, loadedState().rings.first().progress)
    }

    private suspend fun loadedState(): ReadHomeUiState {
        val clock = FixedClock()
        val viewModel = ReadHomeViewModel(
            ObserveReadingHome(content, progress, settings, UnlockedPostCount(content, settings)),
            ObserveStreak(progress, clock),
        )
        return viewModel.uiState.first { !it.isLoading }
    }
}
