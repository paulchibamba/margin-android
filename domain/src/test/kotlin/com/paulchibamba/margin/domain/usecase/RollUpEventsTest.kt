package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.repository.EventSink
import com.paulchibamba.margin.domain.rollup.FakeRollupStore
import com.paulchibamba.margin.domain.tracking.EventFixtures
import com.paulchibamba.margin.domain.tracking.EventType
import com.paulchibamba.margin.domain.tracking.FakeEventLog
import com.paulchibamba.margin.domain.tracking.LoggedEvent
import java.time.Instant
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest

class RollUpEventsTest {
    private val clock = FixedClock(Instant.parse("2026-10-05T09:00:00Z"))
    private val log = FakeEventLog()
    private val sink = object : EventSink {
        val pending = mutableListOf<LoggedEvent>()
        override fun append(event: LoggedEvent) {
            pending += event
        }

        override suspend fun flush() {
            log.logged += pending
            pending.clear()
        }
    }
    private val store = FakeRollupStore()
    private val rollUp = RollUpEvents(clock, sink, log, store, FakeContentRepository())

    private fun impressionAt(at: String) =
        LoggedEvent(Instant.parse(at), null, EventFixtures.forType(EventType.POST_IMPRESSION))

    @Test
    fun `today's rollup includes events still in the buffer`() = runTest {
        log.logged += impressionAt("2026-10-04T23:59:59Z")
        sink.append(impressionAt("2026-10-05T08:00:00Z"))
        sink.append(impressionAt("2026-10-05T08:30:00Z"))

        val today = rollUp.today()

        assertEquals(LocalDate.parse("2026-10-05"), today.date)
        assertEquals(2, today.metrics.posts.postsSeen)
        assertEquals(today, store.saved[today.date])
    }

    @Test
    fun `every day from the first event to yesterday is rolled up once`() = runTest {
        log.logged += impressionAt("2026-10-02T12:00:00Z")
        log.logged += impressionAt("2026-10-04T12:00:00Z")
        log.logged += impressionAt("2026-10-05T08:00:00Z")

        val days = rollUp.missedDays()

        assertEquals(listOf("2026-10-02", "2026-10-03", "2026-10-04").map(LocalDate::parse), days)
        assertEquals(listOf(1, 0, 1), days.map { store.saved.getValue(it).metrics.posts.postsSeen })
        assertEquals(emptyList(), rollUp.missedDays())
    }

    @Test
    fun `a day rolled up before it ended is rolled up again`() = runTest {
        log.logged += impressionAt("2026-10-04T12:00:00Z")
        clock.instant = Instant.parse("2026-10-04T13:00:00Z")
        rollUp.today()
        log.logged += impressionAt("2026-10-04T18:00:00Z")
        clock.instant = Instant.parse("2026-10-05T00:30:00Z")

        val days = rollUp.missedDays()

        assertEquals(listOf(LocalDate.parse("2026-10-04")), days)
        assertEquals(2, store.saved.getValue(days.single()).metrics.posts.postsSeen)
    }

    @Test
    fun `an empty log has nothing to roll up`() = runTest {
        assertEquals(emptyList(), rollUp.missedDays())
    }
}
