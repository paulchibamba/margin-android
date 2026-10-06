package com.paulchibamba.margin.feature.debug

import com.paulchibamba.margin.domain.feed.ConceptProgress
import com.paulchibamba.margin.domain.feed.FeedState
import com.paulchibamba.margin.domain.memory.MemoryCard
import com.paulchibamba.margin.domain.model.ConceptId
import com.paulchibamba.margin.domain.repository.ClockOffsetStore
import com.paulchibamba.margin.domain.time.OffsetClock
import com.paulchibamba.margin.domain.feed.ReviewLogEntry
import com.paulchibamba.margin.domain.feed.cia
import com.paulchibamba.margin.domain.memory.CardState
import com.paulchibamba.margin.domain.memory.Rating
import com.paulchibamba.margin.domain.model.PostId
import com.paulchibamba.margin.domain.progress.RewardKind
import com.paulchibamba.margin.domain.bake.FakeBakeStateStore
import com.paulchibamba.margin.domain.bake.ProgressPostWriter
import com.paulchibamba.margin.domain.llm.FakeApiKeyStore
import com.paulchibamba.margin.domain.llm.FakeLlmClient
import com.paulchibamba.margin.domain.llm.FakeLlmLedger
import com.paulchibamba.margin.domain.llm.FakeLlmSettingsRepository
import com.paulchibamba.margin.domain.usecase.BakeProgressPosts
import com.paulchibamba.margin.domain.usecase.CallLlm
import com.paulchibamba.margin.domain.usecase.DescribeBakes
import com.paulchibamba.margin.domain.usecase.BuildProgressFacts
import com.paulchibamba.margin.domain.usecase.CountDueReviews
import com.paulchibamba.margin.domain.usecase.FakeContentRepository
import com.paulchibamba.margin.domain.usecase.FakeGeneratedPostRepository
import com.paulchibamba.margin.domain.usecase.FakeSettingsRepository
import com.paulchibamba.margin.domain.usecase.FakeProgressRepository
import com.paulchibamba.margin.domain.tracking.FakeEventLog
import com.paulchibamba.margin.domain.tracking.RecentEvent
import com.paulchibamba.margin.domain.tracking.RecordingEventSink
import com.paulchibamba.margin.domain.usecase.FixedClock
import com.paulchibamba.margin.domain.usecase.GetRecentEvents
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.random.Random
import kotlinx.coroutines.test.runTest
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
    private val eventSink = RecordingEventSink()
    private val eventLog = FakeEventLog()
    private val generatedPosts = FakeGeneratedPostRepository()
    private val content = FakeContentRepository()
    private val bakeState = FakeBakeStateStore()
    private val ledger = FakeLlmLedger()
    private val viewModel by lazy {
        DebugToolsViewModel(
            clock,
            CountDueReviews(progress, clock),
            scheduler,
            ResetProgress(progress),
            GetRecentEvents(eventSink, eventLog),
            bakeProgressPosts(),
            DescribeBakes(bakeState, generatedPosts, ledger, clock),
        )
    }

    private fun bakeProgressPosts(): BakeProgressPosts {
        val buildFacts = BuildProgressFacts(content, progress, FakeSettingsRepository(), eventLog, clock)
        val llmSettings = FakeLlmSettingsRepository()
        val callLlm = CallLlm(FakeApiKeyStore(), llmSettings, ledger, FakeLlmClient(), clock)
        val writer = ProgressPostWriter(callLlm, llmSettings, { false }, Random(1))
        return BakeProgressPosts(buildFacts, generatedPosts, content, bakeState, writer, clock, Random(1))
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
    fun `showing events flushes the buffer and lists the latest ones`() {
        eventLog.recentEvents = listOf(
            RecentEvent(Instant.parse("2026-10-01T08:04:05Z"), "session_start", null, "{\"entry\":\"launcher\"}"),
        )

        viewModel.onShowEvents()

        assertEquals(1, eventSink.flushCount)
        assertEquals(listOf("08:04:05 session_start {\"entry\":\"launcher\"}"), viewModel.uiState.value.recentEvents)
    }

    @Test
    fun `baking now stores template progress posts and lists them with their writer`() = runTest {
        val start = systemClock.instant
        listOf(Rating.AGAIN to 6L, Rating.GOOD to 3L, Rating.GOOD to 1L).forEach { (rating, daysAgo) ->
            val at = start.minus(daysAgo, ChronoUnit.DAYS)
            progress.appendReview(ReviewLogEntry(at, cia.id, PostId("quiz"), rating, CardState.REVIEW, 1.0, 1.0, null))
        }

        viewModel.onBakeProgressPosts()

        val baked = generatedPosts.posts
        assertTrue(baked.any { it.kind == RewardKind.Comeback })
        val state = viewModel.uiState.value
        assertEquals("Baked ${baked.size} progress posts", state.bakeReport)
        assertTrue(state.bakeLines.first().startsWith("Last bake: "))
        assertEquals("Spent today: $0.00 · 0 calls", state.bakeLines[1])
        assertTrue(state.bakeLines.any { line -> line.startsWith("comeback · template · ") })

        viewModel.onBakeProgressPosts()
        assertEquals("Nothing new since the last bake", viewModel.uiState.value.bakeReport)
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
        progress.feedState.value = FeedState(rewardAtStep = 99, conceptProgress = concepts)

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
        progress.feedState.value = FeedState(rewardAtStep = 99)

        viewModel.onResetProgress()

        assertTrue(viewModel.uiState.value.isProgressResetArmed)
        assertEquals("Tap again to erase all progress", viewModel.uiState.value.resetProgressLabel)
        assertFalse(viewModel.uiState.value.isProgressCleared)
        assertEquals(FeedState(rewardAtStep = 99), progress.feedState.value)
    }

    @Test
    fun `the second tap clears progress and asks for a restart`() {
        progress.feedState.value = FeedState(rewardAtStep = 99)

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
