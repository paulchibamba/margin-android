package com.paulchibamba.margin.feature.settings.screentime

import com.paulchibamba.margin.domain.rollup.FakeRollupStore
import com.paulchibamba.margin.domain.screentime.AppCategory
import com.paulchibamba.margin.domain.screentime.AppScreenTime
import com.paulchibamba.margin.domain.screentime.FakeScreenTimeStore
import com.paulchibamba.margin.domain.screentime.FakeUsageSource
import com.paulchibamba.margin.domain.screentime.PackageName
import com.paulchibamba.margin.domain.tracking.FakeEventLog
import com.paulchibamba.margin.domain.tracking.RecordingEventSink
import com.paulchibamba.margin.domain.usecase.DeleteScreenTime
import com.paulchibamba.margin.domain.usecase.FakeContentRepository
import com.paulchibamba.margin.domain.usecase.FixedClock
import com.paulchibamba.margin.domain.usecase.IngestScreenTime
import com.paulchibamba.margin.domain.usecase.RollUpEvents
import java.time.Instant
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.minutes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ScreenTimeSettingsViewModelTest {
    private val clock = FixedClock(Instant.parse("2026-10-05T09:00:00Z"))
    private val source = FakeUsageSource().apply { isGranted = false }
    private val store = FakeScreenTimeStore()
    private val rollUp =
        RollUpEvents(clock, RecordingEventSink(), FakeEventLog(), FakeRollupStore(), FakeContentRepository(), store)
    private val ingest = IngestScreenTime(clock, source, store, rollUp)
    private val viewModel = ScreenTimeSettingsViewModel(source, ingest, DeleteScreenTime(clock, store, rollUp))

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `turning screen time on explains it first, and turning it off says the data stays`() {
        viewModel.onResume()
        viewModel.onUseScreenTimeClick()
        assertEquals(ScreenTimeDialog.TURN_ON, viewModel.uiState.value.dialog)

        source.isGranted = true
        viewModel.onDialogDismissed()
        viewModel.onResume()
        viewModel.onUseScreenTimeClick()

        assertEquals(ScreenTimeDialog.TURN_OFF, viewModel.uiState.value.dialog)
    }

    @Test
    fun `coming back with access granted copies the last week straight away`() = runTest {
        viewModel.onResume()
        assertFalse(viewModel.uiState.value.hasAccess)

        source.isGranted = true
        viewModel.onResume()

        assertTrue(viewModel.uiState.value.hasAccess)
        assertEquals(LocalDate.parse("2026-10-04"), store.ingestedThrough)
    }

    @Test
    fun `deleting asks first, then removes every saved day`() = runTest {
        val app = AppScreenTime(PackageName("a"), "A", AppCategory.SOCIAL, isDoom = true, 5.minutes)
        store.saveDay(LocalDate.parse("2026-10-04"), listOf(app))

        viewModel.onDeleteClick()
        assertEquals(ScreenTimeDialog.CONFIRM_DELETE, viewModel.uiState.value.dialog)
        viewModel.onDeleteConfirmed()

        assertNull(viewModel.uiState.value.dialog)
        assertTrue(viewModel.uiState.value.isDeleted)
        assertTrue(store.saved.isEmpty())
    }
}
