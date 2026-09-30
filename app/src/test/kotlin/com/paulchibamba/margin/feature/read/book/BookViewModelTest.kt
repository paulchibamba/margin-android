package com.paulchibamba.margin.feature.read.book

import androidx.lifecycle.SavedStateHandle
import com.paulchibamba.margin.domain.feed.appSec
import com.paulchibamba.margin.domain.feed.noteId
import com.paulchibamba.margin.domain.model.ChapterRef
import com.paulchibamba.margin.domain.progression.ReadingState
import com.paulchibamba.margin.domain.usecase.FakeBookCoverRepository
import com.paulchibamba.margin.domain.usecase.FakeContentRepository
import com.paulchibamba.margin.domain.usecase.FakeCoverImageStore
import com.paulchibamba.margin.domain.usecase.FakeProgressRepository
import com.paulchibamba.margin.domain.usecase.FakeSettingsRepository
import com.paulchibamba.margin.domain.usecase.FixedClock
import com.paulchibamba.margin.domain.usecase.MarkChapterKnown
import com.paulchibamba.margin.domain.usecase.ObserveBook
import com.paulchibamba.margin.domain.usecase.ObserveBookCovers
import com.paulchibamba.margin.domain.usecase.RemoveBookCover
import com.paulchibamba.margin.domain.usecase.SetBookCover
import com.paulchibamba.margin.domain.usecase.UnmarkChapterKnown
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
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class BookViewModelTest {

    private val content = FakeContentRepository()
    private val progress = FakeProgressRepository()
    private val settings = FakeSettingsRepository()
    private val covers = FakeBookCoverRepository()
    private val images = FakeCoverImageStore()
    private val chapterThree = ChapterRef(appSec.slug, 3)

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `marking a chapter known finishes its row and offers undo`() = runTest {
        val viewModel = bookViewModel()

        viewModel.onMarkKnown(chapterThree)

        val state = viewModel.loadedState()
        assertTrue(state.rowOf(3).isDone)
        assertEquals(chapterThree, state.undoChapter)
        assertEquals(setOf(chapterThree), progress.reading.value.knownChapters)
    }

    @Test
    fun `undo forgets the known chapter and hides the snackbar`() = runTest {
        val viewModel = bookViewModel()
        viewModel.onMarkKnown(chapterThree)

        viewModel.onUndo()

        val state = viewModel.loadedState()
        assertFalse(state.rowOf(3).isDone)
        assertNull(state.undoChapter)
        assertEquals(emptySet(), progress.reading.value.knownChapters)
    }

    @Test
    fun `the current chapter is the first one not yet read`() = runTest {
        progress.reading.value = ReadingState(readNotes = setOf(noteId(appSec, 1, 0), noteId(appSec, 1, 1)))

        val state = bookViewModel().loadedState()

        assertEquals(listOf(false, true, false), state.chapters.map { it.isCurrent })
        assertEquals(listOf(false, false, true), state.chapters.map { it.isAhead })
    }

    @Test
    fun `reading-only chapters are tagged and left out of the introduced count`() = runTest {
        val state = bookViewModel().loadedState()

        assertTrue(state.rowOf(2).isReadingOnly)
        assertEquals("Reading only · 0/1", chapterDetailLabel(state.rowOf(2)))
        assertEquals(3, state.conceptCount)
    }

    @Test
    fun `a picked image becomes the book's cover until it is removed`() = runTest {
        val viewModel = bookViewModel()
        assertFalse(viewModel.loadedState().hasCover)

        viewModel.onCoverPicked("content://media/picker/0/1")
        val picked = viewModel.uiState.first { it.hasCover }
        assertEquals(images.files.single(), picked.coverPath)

        viewModel.onRemoveCover()
        assertNull(viewModel.uiState.first { !it.hasCover }.coverPath)
        assertTrue(images.files.isEmpty())
    }

    private fun bookViewModel() = BookViewModel(
        SavedStateHandle(mapOf("slug" to appSec.slug.value)),
        ObserveBook(content, progress, settings),
        ObserveBookCovers(covers),
        MarkChapterKnown(progress, FixedClock()),
        UnmarkChapterKnown(progress),
        SetBookCover(covers, images, FixedClock()),
        RemoveBookCover(covers, images),
    )

    private suspend fun BookViewModel.loadedState() = uiState.first { !it.isLoading }

    private fun BookUiState.rowOf(chapter: Int) = chapters.single { it.chapter.chapter == chapter }
}
