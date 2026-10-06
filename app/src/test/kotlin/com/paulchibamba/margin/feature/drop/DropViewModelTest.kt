package com.paulchibamba.margin.feature.drop

import com.paulchibamba.margin.domain.drop.DropCompletion
import com.paulchibamba.margin.domain.drop.DropEvidence
import com.paulchibamba.margin.domain.drop.DropPage
import com.paulchibamba.margin.domain.drop.DropSession
import com.paulchibamba.margin.domain.tracking.Event
import com.paulchibamba.margin.domain.tracking.PostAttention
import com.paulchibamba.margin.feature.feed.FakeClock
import com.paulchibamba.margin.feature.feed.FakeFeedUseCases
import com.paulchibamba.margin.feature.feed.itemOf
import com.paulchibamba.margin.feature.feed.tipPost
import kotlin.random.Random
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DropViewModelTest {
    private val useCases = FakeFeedUseCases()
    private val session = DropSession(size = 5, pages = (2..4).map { index -> DropPage(index, itemOf(tipPost(index))) })
    private val drops = FakeDropUseCases(session)
    private val clock = FakeClock()
    private val recorded = mutableListOf<Event>()
    private val viewModel by lazy {
        DropViewModel(useCases, drops, Random(seed = 17), clock, PostAttention(clock, recorded::add))
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
    fun `the drop resumes at the first item not done, then ends on completion and continue pages`() {
        val state = viewModel.uiState.value

        assertEquals(listOf(2, 3, 4), state.pages.map { it.dropIndex })
        assertEquals(DropRun(size = 5), state.drop)
        assertEquals(5, state.pageCount)
        assertTrue(state.isDropCompletionPage(3))
        assertTrue(state.isDropContinuePage(4))
        assertEquals(0, useCases.nextPostCalls)
    }

    @Test
    fun `entering an item records it once with its feed step`() {
        viewModel.onPageEntered(0)
        viewModel.onPageEntered(1)
        viewModel.onPageEntered(0)

        assertEquals(listOf(2, 3), drops.entered)
        assertEquals(102, viewModel.uiState.value.pages[0].item.step)
    }

    @Test
    fun `leaving an item records the exit and moves the drop on`() {
        viewModel.onPageEntered(0)
        viewModel.onPageLeft(0, 4.seconds)
        viewModel.onPageLeft(0, 4.seconds)

        assertEquals(listOf(2), drops.left)
        assertEquals(1, useCases.exits.size)
    }

    @Test
    fun `reaching the completion page shows the streak and today's evidence`() {
        assertNull(viewModel.uiState.value.drop?.completion)

        viewModel.onPageEntered(3)

        assertEquals(DropCompletion(4, DropEvidence.Remembered(2)), viewModel.uiState.value.drop?.completion)
    }

    @Test
    fun `keep going and close are recorded`() {
        viewModel.onContinue()
        viewModel.onDismiss()

        assertEquals(1, drops.continued)
        assertEquals(1, drops.dismissed)
    }

    @Test
    fun `a finished drop opens straight onto its completion page`() {
        drops.session = DropSession(size = 5, pages = emptyList())

        val state = viewModel.uiState.value

        assertTrue(state.isDropCompletionPage(0))
        assertEquals(false, state.isLoading)
    }
}
