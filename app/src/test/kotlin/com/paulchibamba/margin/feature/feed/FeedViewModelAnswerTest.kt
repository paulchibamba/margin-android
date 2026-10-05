package com.paulchibamba.margin.feature.feed

import com.paulchibamba.margin.domain.memory.Rating
import com.paulchibamba.margin.domain.signals.AnswerOutcome
import com.paulchibamba.margin.domain.signals.ExpectedReadTime
import com.paulchibamba.margin.domain.tracking.Event
import com.paulchibamba.margin.domain.tracking.PostAttention
import com.paulchibamba.margin.feature.feed.post.TestResponse
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
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class FeedViewModelAnswerTest {

    private val useCases = FakeFeedUseCases(listOf(mcqPost(0), recallPost(1), mcqPost(2)))
    private val clock = FakeClock()
    private val recorded = mutableListOf<Event>()
    private val attention = PostAttention(clock, recorded::add)
    private val expectedReadTime = ExpectedReadTime.of(mcqPost(0).content)

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `a wrong answer grades the concept Again`() {
        val viewModel = feedViewModelOnFirstPage()
        clock.advanceBy(3.seconds)

        viewModel.onRespond(0, TestResponse.Choice(0))

        assertEquals(Rating.AGAIN, answerOn(viewModel, 0)?.rating)
    }

    @Test
    fun `an answer is recorded with whether it was right, how long it took and its grade`() {
        val viewModel = feedViewModelOnFirstPage()
        clock.advanceBy(3.seconds)

        viewModel.onRespond(0, TestResponse.Choice(0))

        val answer = recorded.filterIsInstance<Event.PostAnswer>().single()
        assertEquals(false, answer.isCorrect)
        assertEquals(3.seconds, answer.timeToAnswer)
        assertEquals(Rating.AGAIN, answer.grade)
    }

    @Test
    fun `a right answer that takes more than twice the reading time grades it Hard`() {
        val viewModel = feedViewModelOnFirstPage()
        clock.advanceBy(expectedReadTime * 2 + 1.seconds)

        viewModel.onRespond(0, TestResponse.Choice(1))

        assertEquals(Rating.HARD, answerOn(viewModel, 0)?.rating)
    }

    @Test
    fun `a right answer in normal time grades it Good and never Easy`() {
        val viewModel = feedViewModelOnFirstPage()
        clock.advanceBy(1.seconds)

        viewModel.onRespond(0, TestResponse.Choice(1))

        assertEquals(Rating.GOOD, answerOn(viewModel, 0)?.rating)
    }

    @Test
    fun `leaving an answered post records the answer and the time it took, not the time on the page`() {
        val viewModel = feedViewModelOnFirstPage()
        clock.advanceBy(4.seconds)
        viewModel.onRespond(0, TestResponse.Choice(1))

        viewModel.onPageLeft(0, 30.seconds)

        val exit = useCases.exits.single().second
        assertEquals(AnswerOutcome.Correct, exit.answer)
        assertEquals(4.seconds, exit.timeToAnswer)
        assertEquals(30.seconds, exit.dwell)
        assertTrue(exit.isEngaged)
    }

    @Test
    fun `only the first answer on a post counts`() {
        val viewModel = feedViewModelOnFirstPage()

        viewModel.onRespond(0, TestResponse.Choice(0))
        viewModel.onRespond(0, TestResponse.Choice(1))

        assertEquals(TestResponse.Choice(0), answerOn(viewModel, 0)?.response)
    }

    @Test
    fun `a self-grade is taken as given`() {
        val viewModel = feedViewModelOnFirstPage()
        viewModel.onPageEntered(1)

        viewModel.onRespond(1, TestResponse.SelfGrade(Rating.HARD))

        assertEquals(AnswerOutcome.SelfGraded(Rating.HARD), answerOn(viewModel, 1)?.outcome)
    }

    @Test
    fun `self-grade intervals come from the scheduler preview for the concept`() {
        useCases.intervals = mapOf(Rating.AGAIN to 3.minutes, Rating.HARD to 2.days, Rating.GOOD to 6.days)
        val viewModel = feedViewModelOnFirstPage()

        viewModel.onPageEntered(1)

        assertEquals(useCases.intervals, viewModel.uiState.value.pages[1].intervals)
    }

    @Test
    fun `after grading a snackbar says when the concept comes back`() {
        useCases.intervals = mapOf(Rating.AGAIN to 10.minutes, Rating.HARD to 2.days, Rating.GOOD to 6.days)
        val viewModel = feedViewModelOnFirstPage()

        viewModel.onRespond(0, TestResponse.Choice(3))

        val nudge = viewModel.uiState.value.nudge
        assertEquals(FeedNudgeKind.SEE_AGAIN, nudge?.kind)
        assertEquals("You'll see this again in ~10 min", nudge?.title)
        assertEquals("Alice and Bob Learn Application Security · Ch 1 · 1/10", nudge?.detail)
    }

    @Test
    fun `a concept without a memory card grades without a snackbar`() {
        useCases.intervals = emptyMap()
        val viewModel = feedViewModelOnFirstPage()

        viewModel.onRespond(0, TestResponse.Choice(1))

        assertNull(viewModel.uiState.value.nudge)
    }

    private fun feedViewModelOnFirstPage(): FeedViewModel =
        FeedViewModel(useCases, Random(seed = 17), clock, attention).apply { onPageEntered(0) }

    private fun answerOn(viewModel: FeedViewModel, index: Int): TestAnswer? =
        viewModel.uiState.value.pages[index].answer
}
