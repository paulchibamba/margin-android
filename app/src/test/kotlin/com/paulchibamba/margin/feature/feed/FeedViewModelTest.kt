package com.paulchibamba.margin.feature.feed

import com.paulchibamba.margin.domain.actions.PostAction
import com.paulchibamba.margin.domain.feed.Confidence
import com.paulchibamba.margin.domain.signals.PostExit
import com.paulchibamba.margin.domain.usecase.StreakSummary
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlin.random.Random
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class FeedViewModelTest {

    private val useCases = FakeFeedUseCases()
    private val clock = FakeClock()

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `the first page and one page ahead load before the user swipes`() {
        val viewModel = feedViewModel()

        assertEquals(listOf("post-0", "post-1"), postIdsOf(viewModel))
        assertEquals(2, useCases.nextPostCalls)
    }

    @Test
    fun `entering a page loads one more page ahead`() {
        val viewModel = feedViewModel()

        viewModel.onPageEntered(1)

        assertEquals(listOf("post-0", "post-1", "post-2"), postIdsOf(viewModel))
        assertEquals(1, viewModel.uiState.value.currentIndex)
    }

    @Test
    fun `entering a page that already has a page ahead loads nothing`() {
        val viewModel = feedViewModel()

        viewModel.onPageEntered(0)

        assertEquals(2, useCases.nextPostCalls)
    }

    @Test
    fun `no two pages in a row share a skin`() {
        val viewModel = feedViewModel()
        (1..8).forEach(viewModel::onPageEntered)

        val skins = viewModel.uiState.value.pages.map { it.skin }
        skins.zipWithNext().forEach { (previous, next) -> assertNotEquals(previous, next) }
    }

    @Test
    fun `leaving a page records its dwell and counts an action as engagement`() {
        val viewModel = feedViewModel()
        viewModel.onAction(0, PostAction.SAVE)

        viewModel.onPageLeft(0, 5.seconds)

        val (post, exit) = useCases.exits.single()
        assertEquals("post-0", post.id.value)
        assertEquals(PostExit(5.seconds, isEngaged = true, answer = null, isMarkedLess = false), exit)
    }

    @Test
    fun `an interaction reported by the post counts as engagement when the page is left`() {
        val viewModel = feedViewModel()
        viewModel.onEngaged(0)

        viewModel.onPageLeft(0, 4.seconds)

        assertTrue(useCases.exits.single().second.isEngaged)
    }

    @Test
    fun `an interaction on one page does not engage the next`() {
        val viewModel = feedViewModel()
        viewModel.onEngaged(0)

        viewModel.onPageLeft(1, 4.seconds)

        assertFalse(useCases.exits.single().second.isEngaged)
    }

    @Test
    fun `leaving a page without acting records an unengaged exit`() {
        val viewModel = feedViewModel()

        viewModel.onPageLeft(0, 800.milliseconds)

        assertFalse(useCases.exits.single().second.isEngaged)
    }

    @Test
    fun `a page marked Less leaves as unengaged and marked less`() {
        val viewModel = feedViewModel()
        viewModel.onAction(0, PostAction.LESS)

        viewModel.onPageLeft(0, 2.seconds)

        val exit = useCases.exits.single().second
        assertFalse(exit.isEngaged)
        assertTrue(exit.isMarkedLess)
    }

    @Test
    fun `a page's exit is recorded only the first time it is left`() {
        val viewModel = feedViewModel()

        viewModel.onPageLeft(0, 3.seconds)
        viewModel.onPageLeft(0, 4.seconds)

        assertEquals(1, useCases.exits.size)
    }

    @Test
    fun `Got it and Lost are mutually exclusive on a post`() {
        val viewModel = feedViewModel()

        viewModel.onAction(0, PostAction.GOT)
        viewModel.onAction(0, PostAction.LOST)

        assertEquals(listOf(PostAction.GOT), useCases.actions.map { it.second })
        assertEquals(Confidence.GOT, viewModel.uiState.value.pages[0].viewState.confidence)
    }

    @Test
    fun `Lost on one post leaves Got it available on the next`() {
        val viewModel = feedViewModel()

        viewModel.onAction(0, PostAction.LOST)
        viewModel.onAction(1, PostAction.GOT)

        assertEquals(listOf(PostAction.LOST, PostAction.GOT), useCases.actions.map { it.second })
    }

    @Test
    fun `Lost shows a nudge that another angle is coming`() {
        val viewModel = feedViewModel()

        viewModel.onAction(0, PostAction.LOST)

        val nudge = viewModel.uiState.value.nudge
        assertEquals(FeedNudgeKind.ANOTHER_ANGLE_COMING, nudge?.kind)
        assertEquals("Got it. A different angle is next.", nudge?.title)
        assertEquals("No quiz on Concept concept-0 until it clicks.", nudge?.detail)
        assertEquals(0, nudge?.pageIndex)
    }

    @Test
    fun `Got it shows a nudge that a quick check is coming`() {
        val viewModel = feedViewModel()

        viewModel.onAction(0, PostAction.GOT)

        val nudge = viewModel.uiState.value.nudge
        assertEquals(FeedNudgeKind.TEST_COMING_SOON, nudge?.kind)
        assertEquals("Nice. A quick check is coming up.", nudge?.title)
        assertEquals("No more explanations of Concept concept-0 for now.", nudge?.detail)
    }

    @Test
    fun `Save shows no nudge`() {
        val viewModel = feedViewModel()

        viewModel.onAction(0, PostAction.SAVE)

        assertNull(viewModel.uiState.value.nudge)
    }

    @Test
    fun `swiping to another page dismisses the nudge`() {
        val viewModel = feedViewModel()
        viewModel.onAction(0, PostAction.LOST)

        viewModel.onPageEntered(1)

        assertNull(viewModel.uiState.value.nudge)
    }

    @Test
    fun `reading the source is recorded as a Read action`() {
        val viewModel = feedViewModel()

        viewModel.onReadSource(0)

        assertEquals(PostAction.READ, useCases.actions.single().second)
    }

    @Test
    fun `More opens why-this-post for the current page and dismissing closes it`() {
        val viewModel = feedViewModel()
        viewModel.onPageEntered(1)

        viewModel.onMore()
        assertEquals("post-1", viewModel.uiState.value.sheetPage?.item?.post?.id?.value)

        viewModel.onSheetDismiss()
        assertNull(viewModel.uiState.value.sheetPage)
    }

    @Test
    fun `when the engine runs dry the caught-up page follows the last post`() {
        useCases.upcoming.clear()
        useCases.upcoming.add(tipPost(0))

        val state = feedViewModel().uiState.value

        assertEquals(1, state.pages.size)
        assertTrue(state.isCaughtUp)
        assertEquals(2, state.pageCount)
        assertTrue(state.isCaughtUpPage(1))
    }

    @Test
    fun `a caught-up feed carries on once reading unlocks more posts`() {
        useCases.upcoming.clear()
        val viewModel = feedViewModel()
        useCases.upcoming.addAll(listOf(tipPost(7), tipPost(8)))

        viewModel.onCaughtUpShown()

        assertEquals(listOf("post-7", "post-8"), postIdsOf(viewModel))
        assertFalse(viewModel.uiState.value.isCaughtUp)
    }

    @Test
    fun `the streak follows the recorded activity`() {
        val viewModel = feedViewModel()

        useCases.streak.value = StreakSummary(currentStreak = 12, week = emptyList())

        assertEquals(12, viewModel.uiState.value.streak)
    }

    private fun feedViewModel() = FeedViewModel(useCases, Random(seed = 17), clock)

    private fun postIdsOf(viewModel: FeedViewModel) = viewModel.uiState.value.pages.map { it.item.post.id.value }
}
