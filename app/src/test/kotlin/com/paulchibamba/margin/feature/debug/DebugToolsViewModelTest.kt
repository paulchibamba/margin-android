package com.paulchibamba.margin.feature.debug

import com.paulchibamba.margin.domain.feed.ConceptProgress
import com.paulchibamba.margin.domain.feed.FeedState
import com.paulchibamba.margin.domain.memory.MemoryCard
import com.paulchibamba.margin.domain.model.ConceptId
import com.paulchibamba.margin.domain.repository.ClockOffsetStore
import com.paulchibamba.margin.domain.time.OffsetClock
import com.paulchibamba.margin.domain.usecase.CountDueReviews
import com.paulchibamba.margin.domain.usecase.FakeProgressRepository
import com.paulchibamba.margin.domain.usecase.FixedClock
import com.paulchibamba.margin.domain.usecase.ResetProgress
import com.paulchibamba.margin.feature.settings.FakeReminderScheduler
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.toJavaDuration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DebugToolsViewModelTest {

    private val systemClock = FixedClock()
    private val clock = OffsetClock(systemClock, MemoryOffsetStore())
    private val progress = FakeProgressRepository()
    private val scheduler = FakeReminderScheduler()
    private val viewModel by lazy {
        DebugToolsViewModel(clock, CountDueReviews(progress, clock), scheduler, ResetProgress(progress))
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
    fun `advancing moves the clock and shows the offset`() {
        viewModel.onAdvance(1.hours)
        viewModel.onAdvance(1.days)

        assertEquals(25.hours, viewModel.uiState.value.offset)
        assertEquals(systemClock.instant.plus(25.hours.toJavaDuration()), clock.now())
    }

    @Test
    fun `resetting returns to real time`() {
        viewModel.onAdvance(1.days)

        viewModel.onResetClock()

        assertEquals("Clock: real time", viewModel.uiState.value.clockLabel)
        assertEquals(systemClock.instant, clock.now())
    }

    @Test
    fun `the due count is taken at the moved clock`() {
        val concepts = mapOf(ConceptId("cia") to cardDueIn(2.days))
        progress.feedState.value = FeedState(delightAtStep = 99, conceptProgress = concepts)

        viewModel.onShowDueCount()
        assertEquals("Nothing is due", viewModel.uiState.value.dueCountLabel)

        viewModel.onAdvance(2.days)
        assertNull(viewModel.uiState.value.dueCount)
        viewModel.onShowDueCount()
        assertEquals("1 concept is due", viewModel.uiState.value.dueCountLabel)
    }

    @Test
    fun `showing the tools again forgets the last due count`() {
        viewModel.onShowDueCount()

        viewModel.onShown()

        assertNull(viewModel.uiState.value.dueCount)
    }

    @Test
    fun `sending a reminder runs the reminder check once`() {
        viewModel.onSendReviewReminder()

        assertEquals(1, scheduler.runCount)
    }

    @Test
    fun `the first tap on reset progress only arms it and clears nothing`() {
        progress.feedState.value = FeedState(delightAtStep = 99)

        viewModel.onResetProgress()

        assertTrue(viewModel.uiState.value.isProgressResetArmed)
        assertEquals("Tap again to erase all progress", viewModel.uiState.value.resetProgressLabel)
        assertFalse(viewModel.uiState.value.isProgressCleared)
        assertEquals(FeedState(delightAtStep = 99), progress.feedState.value)
    }

    @Test
    fun `the second tap clears progress and asks for a restart`() {
        progress.feedState.value = FeedState(delightAtStep = 99)

        viewModel.onResetProgress()
        viewModel.onResetProgress()

        assertNull(progress.feedState.value)
        assertTrue(viewModel.uiState.value.isProgressCleared)
        assertFalse(viewModel.uiState.value.isProgressResetArmed)
    }

    private fun cardDueIn(duration: Duration): ConceptProgress {
        val due = systemClock.instant.plus(duration.toJavaDuration())
        return ConceptProgress(introducedAtStep = 1, card = MemoryCard.new(systemClock.instant).copy(due = due))
    }

    private class MemoryOffsetStore(private var offset: Duration = Duration.ZERO) : ClockOffsetStore {
        override fun load(): Duration = offset
        override fun save(offset: Duration) {
            this.offset = offset
        }
    }
}
