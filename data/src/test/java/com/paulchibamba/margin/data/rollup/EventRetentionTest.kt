package com.paulchibamba.margin.data.rollup

import com.paulchibamba.margin.data.database.DatabaseTest
import com.paulchibamba.margin.data.tracking.RoomEventLog
import com.paulchibamba.margin.data.tracking.RoomEventSink
import com.paulchibamba.margin.domain.tracking.EventFixtures
import com.paulchibamba.margin.domain.tracking.EventType
import com.paulchibamba.margin.domain.tracking.LoggedEvent
import com.paulchibamba.margin.domain.usecase.FakeContentRepository
import com.paulchibamba.margin.domain.usecase.FixedClock
import com.paulchibamba.margin.domain.usecase.PruneEventLog
import com.paulchibamba.margin.domain.usecase.RollUpEvents
import java.time.Instant
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class EventRetentionTest : DatabaseTest() {
    private val clock = FixedClock(Instant.parse("2026-10-05T09:00:00Z"))
    private val log by lazy { RoomEventLog(database) }
    private val sink by lazy { RoomEventSink(database, TestScope()) }
    private val store by lazy { RoomRollupStore(database) }
    private val prune by lazy {
        PruneEventLog(clock, log, RollUpEvents(clock, sink, log, store, FakeContentRepository()))
    }

    private fun impressionAt(at: String) =
        LoggedEvent(Instant.parse(at), null, EventFixtures.forType(EventType.POST_IMPRESSION))

    @Test
    fun `retention deletes only events older than 180 days and keeps their rollups`() = runTest {
        listOf("2026-03-01T12:00:00Z", "2026-04-08T08:59:59Z", "2026-04-08T09:00:00Z", "2026-10-05T08:00:00Z")
            .forEach { at -> sink.append(impressionAt(at)) }
        sink.flush()

        assertEquals(2, prune())

        val kept = log.between(Instant.EPOCH, clock.instant).map { it.at.toString() }
        assertEquals(listOf("2026-04-08T09:00:00Z", "2026-10-05T08:00:00Z"), kept)
        val rollups = store.observeFrom(LocalDate.parse("2026-03-01")).first().associateBy { it.date.toString() }
        assertEquals(1, rollups.getValue("2026-03-01").metrics.posts.postsSeen)
        assertEquals(2, rollups.getValue("2026-04-08").metrics.posts.postsSeen)
    }
}
