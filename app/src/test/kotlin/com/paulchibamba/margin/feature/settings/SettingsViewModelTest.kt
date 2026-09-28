package com.paulchibamba.margin.feature.settings

import com.paulchibamba.margin.domain.feed.aiSecurity
import com.paulchibamba.margin.domain.feed.appSec
import com.paulchibamba.margin.domain.feed.defaultSettings
import com.paulchibamba.margin.domain.feed.grokking
import com.paulchibamba.margin.domain.model.Book
import com.paulchibamba.margin.domain.model.BookSettings
import com.paulchibamba.margin.domain.model.BookSlug
import com.paulchibamba.margin.domain.model.Priority
import com.paulchibamba.margin.domain.usecase.FakeContentRepository
import com.paulchibamba.margin.domain.usecase.FakeSettingsRepository
import com.paulchibamba.margin.domain.usecase.ObserveLearningSettings
import com.paulchibamba.margin.domain.usecase.SetDesiredRetention
import com.paulchibamba.margin.domain.usecase.UpdateBookSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private val fourthBook = Book(BookSlug("threat-modeling"), "Threat Modeling")
    private val content = FakeContentRepository(books = listOf(appSec, grokking, aiSecurity, fourthBook))
    private val settings = FakeSettingsRepository(defaultSettings + BookSettings(fourthBook.slug, false, Priority.LOW))

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `you can't deactivate the last active book`() = runTest {
        settings.bookSettings.value = settings.bookSettings.value.map { it.copy(isActive = it.bookSlug == appSec.slug) }
        val viewModel = settingsViewModel()

        val onlyActive = viewModel.loadedState().bookOf(appSec)
        viewModel.onBookSettingsChange(onlyActive.settings.copy(isActive = false))

        assertFalse(onlyActive.canToggle)
        assertTrue(viewModel.loadedState().bookOf(appSec).settings.isActive)
        assertEquals(1, viewModel.loadedState().activeCount)
    }

    @Test
    fun `you can't activate a fourth book`() = runTest {
        val viewModel = settingsViewModel()
        viewModel.onBookSettingsChange(viewModel.loadedState().bookOf(aiSecurity).settings.copy(isActive = true))

        val fourth = viewModel.loadedState().bookOf(fourthBook)
        viewModel.onBookSettingsChange(fourth.settings.copy(isActive = true))

        assertFalse(fourth.canToggle)
        assertFalse(viewModel.loadedState().bookOf(fourthBook).settings.isActive)
        assertEquals(3, viewModel.loadedState().activeCount)
    }

    @Test
    fun `a new priority is saved`() = runTest {
        val viewModel = settingsViewModel()

        viewModel.onBookSettingsChange(viewModel.loadedState().bookOf(grokking).settings.copy(priority = Priority.LOW))

        assertEquals(Priority.LOW, settings.bookSettings().first { it.bookSlug == grokking.slug }.priority)
        assertEquals(Priority.LOW, viewModel.loadedState().bookOf(grokking).settings.priority)
    }

    @Test
    fun `retention follows the slider and is saved when the drag ends`() = runTest {
        val viewModel = settingsViewModel()

        viewModel.onRetentionChange(0.9340001)
        assertEquals(0.93, viewModel.loadedState().retention)
        assertEquals(0.9, settings.retention.value)

        viewModel.onRetentionChangeFinished()
        assertEquals(0.93, settings.retention.value)
    }

    private fun TestScope.settingsViewModel(): SettingsViewModel {
        val viewModel = SettingsViewModel(
            ObserveLearningSettings(content, settings),
            UpdateBookSettings(settings),
            SetDesiredRetention(settings),
        )
        backgroundScope.launch { viewModel.uiState.collect() }
        return viewModel
    }

    private suspend fun SettingsViewModel.loadedState() = uiState.first { !it.isLoading }

    private fun SettingsUiState.bookOf(book: Book) = books.single { it.settings.bookSlug == book.slug }
}
