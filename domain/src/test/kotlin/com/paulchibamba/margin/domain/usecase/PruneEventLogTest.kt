package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.rollup.FakeRollupStore
import com.paulchibamba.margin.domain.screentime.FakeScreenTimeStore
import com.paulchibamba.margin.domain.tracking.EventFixtures
import com.paulchibamba.margin.domain.tracking.EventType
import com.paulchibamba.margin.domain.tracking.FakeEventLog
import com.paulchibamba.margin.domain.tracking.LoggedEvent
import com.paulchibamba.margin.domain.tracking.RecordingEventSink
import java.time.Instant
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class PruneEventLogTest {
    private val clock = FixedClock(Instant.parse("2026-10-05T09:00:00Z"))
    private val log = FakeEventLog()
    private val store = FakeRollupStore()
    private val rollUp =
        RollUpEvents(clock, RecordingEventSink(), log, store, FakeContentRepository(), FakeScreenTimeStore())
    private val prune = PruneEventLog(clock, log, rollUp)

    private fun impressionAt(at: String) =
        LoggedEvent(Instant.parse(at), null, EventFixtures.forType(EventType.POST_IMPRESSION))

    @Test
    fun `only events older than 180 days are deleted`() = runTest {
        val old = impressionAt("2026-04-08T08:59:59Z")
        val kept = impressionAt("2026-04-08T09:00:00Z")
        log.logged += listOf(old, kept)

        assertEquals(1, prune())

        assertEquals(listOf(kept), log.logged)
    }

    @Test
    fun `the rollup of a pruned day survives`() = runTest {
        log.logged += impressionAt("2026-03-01T12:00:00Z")

        prune()

        assertTrue(log.logged.isEmpty())
        assertEquals(1, store.saved.getValue(LocalDate.parse("2026-03-01")).metrics.posts.postsSeen)
    }
}
