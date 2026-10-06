package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.bake.BakeState
import com.paulchibamba.margin.domain.bake.FakeBakeStateStore
import com.paulchibamba.margin.domain.drop.DropHeadline
import com.paulchibamba.margin.domain.drop.DropSlot
import com.paulchibamba.margin.domain.drop.DropStage
import com.paulchibamba.margin.domain.drop.DropStatus
import com.paulchibamba.margin.domain.feed.Candidate
import com.paulchibamba.margin.domain.feed.CandidateSource
import com.paulchibamba.margin.domain.feed.FeedResult
import com.paulchibamba.margin.domain.feed.cia
import com.paulchibamba.margin.domain.feed.generatedPostOf
import com.paulchibamba.margin.domain.feed.leastPrivilege
import com.paulchibamba.margin.domain.feed.sameOrigin
import com.paulchibamba.margin.domain.feed.withIntroduced
import com.paulchibamba.margin.domain.model.ChapterRef
import com.paulchibamba.margin.domain.progress.RewardKind
import com.paulchibamba.margin.domain.tracking.Event
import com.paulchibamba.margin.domain.tracking.EventRecorder
import java.time.Duration
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest

class DropUseCasesTest {
    private val fixture = UseCaseFixture()
    private val drops = fixture.drops
    private val bakeState = FakeBakeStateStore()
    private val events = mutableListOf<Event.DropEvent>()
    private val recorder = EventRecorder { event -> (event as? Event.DropEvent)?.let(events::add) }
    private val stateSource = FeedStateSource(fixture.progress, fixture.settings, fixture.engines)
    private val buildFacts =
        BuildProgressFacts(fixture.content, fixture.progress, fixture.settings, fixture.eventLog, fixture.clock)
    private val prepare = with(fixture) {
        PrepareTodaysDrop(
            drops, libraryLoader, stateSource, settings, engines, buildFacts, generatedPosts, bakeState, recorder,
            clock, lock, Random(5),
        )
    }
    private val dropPosts = DropPosts(fixture.content, fixture.generatedPosts)
    private val open = with(fixture) { OpenDrop(prepare, stateSource, dropPosts, settings, engines, recorder, clock) }
    private val enter = with(fixture) {
        EnterDropItem(drops, stateSource, progress, generatedPosts, settings, engines, clock, lock)
    }
    private val leave = LeaveDropItem(drops, stateSource, recorder, fixture.clock, fixture.lock)
    private val observe = ObserveTodaysDrop(drops, fixture.progress, fixture.clock)
    private val zoomOut = generatedPostOf(RewardKind.ZoomOut, cia, index = 1)
    private val comeback = generatedPostOf(RewardKind.Comeback, leastPrivilege, index = 2)

    private suspend fun withLearner() {
        fixture.markChapterKnown(ChapterRef(cia.bookSlug, 1))
        val state = fixture.engines.feedEngine(0.9).startingState().withIntroduced(cia, leastPrivilege, sameOrigin)
        fixture.progress.feedState.value = state.copy(step = 20)
        fixture.generatedPosts.insert(listOf(zoomOut, comeback))
    }

    @Test
    fun `today's drop is composed once and kept for the whole day`() = runTest {
        withLearner()
        val first = assertNotNull(prepare())

        fixture.generatedPosts.insert(listOf(generatedPostOf(RewardKind.Milestone, sameOrigin, index = 3)))
        fixture.clock.instant = fixture.clock.instant.plus(Duration.ofHours(10))

        assertEquals(first, prepare())
        assertEquals(listOf(DropStage.SHOWN), events.map { it.stage })
        assertEquals(DropSlot.CLOSING, first.items.last().slot)
    }

    @Test
    fun `the bake headline moves into the drop when its post closes it`() = runTest {
        withLearner()
        bakeState.state = BakeState(nextDropHeadline = DropHeadline("3 passes since 25 Sep", zoomOut.id))

        val drop = assertNotNull(prepare())

        assertEquals(zoomOut.id, drop.items.last().postId)
        assertEquals("3 passes since 25 Sep", drop.headline)
        assertNull(bakeState.state.nextDropHeadline)
    }

    @Test
    fun `entering an item records one feed step, and leaving it moves the drop on`() = runTest {
        withLearner()
        val page = assertNotNull(open()).pages.first()
        val candidate = Candidate(page.item.post, page.item.source)

        val item = assertNotNull(enter(page.index, candidate))
        assertNull(enter(page.index, candidate))

        assertEquals(21, item.step)
        assertEquals(21, fixture.progress.feedState.value?.step)
        val size = drops.drops.value.values.single().size
        assertEquals(DropStatus(done = 1, size = size), leave(page.index))
        assertEquals(DropStatus(done = 1, size = size), observe().first())
    }

    @Test
    fun `finishing every item completes the drop and leaves the streak to activity`() = runTest {
        withLearner()
        val session = assertNotNull(open())

        session.pages.forEach { page -> enter(page.index, Candidate(page.item.post, page.item.source)) }
        session.pages.forEach { page -> leave(page.index) }

        assertNotNull(drops.drops.value.values.single().completedAt)
        assertTrue(fixture.progress.activity.value.isEmpty())
        assertEquals(DropStage.COMPLETED, events.last().stage)
        assertNull(assertNotNull(open()).pages.firstOrNull())
    }

    @Test
    fun `the feed holds back the drop's remaining items and their reviews`() = runTest {
        withLearner()
        val drop = assertNotNull(prepare())
        val reviewed = drop.items.filter { it.slot == DropSlot.REVIEW }.map { it.postId.value.substringBeforeLast('/') }

        val served = List(8) { fixture.getNextPost() }.filterIsInstance<FeedResult.Next>().map { it.item }

        assertTrue(served.none { item -> item.post.id in drop.items.map { it.postId } })
        val reviews = served.filter { item -> item.source == CandidateSource.REVIEW }
        assertTrue(reviews.none { item -> item.post.conceptId.value in reviewed })
        assertEquals(drop.size, drops.drops.value.values.single().remainingIndices(stateSource.current()).size)
    }

    @Test
    fun `nothing to show means no drop`() = runTest {
        fixture.settings.bookSettings.value = fixture.settings.bookSettings.value.map { it.copy(isActive = false) }

        assertNull(prepare())
        assertTrue(drops.drops.value.isEmpty())
    }
}
