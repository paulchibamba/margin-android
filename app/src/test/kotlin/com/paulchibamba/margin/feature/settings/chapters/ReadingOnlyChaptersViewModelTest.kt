package com.paulchibamba.margin.feature.settings.chapters

import com.paulchibamba.margin.domain.feed.appSec
import com.paulchibamba.margin.domain.model.ChapterRef
import com.paulchibamba.margin.domain.usecase.FakeContentRepository
import com.paulchibamba.margin.domain.usecase.FakeSettingsRepository
import com.paulchibamba.margin.domain.usecase.ObserveLearningSettings
import com.paulchibamba.margin.domain.usecase.SetChapterReadingOnly
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class ReadingOnlyChaptersViewModelTest {

    private val settings = FakeSettingsRepository()
    private val viewModel by lazy {
        ReadingOnlyChaptersViewModel(
            ObserveLearningSettings(FakeContentRepository(), settings),
            SetChapterReadingOnly(settings),
        )
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `ticking a chapter makes it reading-only and unticking brings it back`() = runTest {
        backgroundScope.launch { viewModel.uiState.collect() }

        viewModel.onReadingOnlyChange(ChapterRef(appSec.slug, 1), isReadingOnly = true)
        assertEquals(listOf(true, true, false), appSecFlags())

        viewModel.onReadingOnlyChange(ChapterRef(appSec.slug, 2), isReadingOnly = false)
        assertEquals(listOf(true, false, false), appSecFlags())
    }

    private suspend fun appSecFlags() = viewModel.uiState.first { !it.isLoading }
        .books.single { it.book == appSec }.chapters.map { it.isReadingOnly }
}
