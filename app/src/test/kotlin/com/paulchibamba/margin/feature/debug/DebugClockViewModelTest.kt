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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.toJavaDuration

@OptIn(ExperimentalCoroutinesApi::class)
class DebugClockViewModelTest {

    private val systemClock = FixedClock()
    private val clock = OffsetClock(systemClock, MemoryOffsetStore())
    private val progress = FakeProgressRepository()
    private val viewModel by lazy { DebugClockViewModel(clock, CountDueReviews(progress, clock)) }

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

        viewModel.onReset()

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
