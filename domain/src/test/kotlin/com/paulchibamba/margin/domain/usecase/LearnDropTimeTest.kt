package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.drop.FakeDropTimeStore
import com.paulchibamba.margin.domain.tracking.Event
import com.paulchibamba.margin.domain.tracking.FakeEventLog
import com.paulchibamba.margin.domain.tracking.LoggedEvent
import com.paulchibamba.margin.domain.tracking.RecordingEventSink
import com.paulchibamba.margin.domain.tracking.SessionEntry
import java.time.Instant
import java.time.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest

class LearnDropTimeTest {
    private val clock = FixedClock(Instant.parse("2026-10-20T10:00:00Z"))
    private val sink = RecordingEventSink()
    private val log = FakeEventLog()
    private val store = FakeDropTimeStore()
    private val learnDropTime = LearnDropTime(clock, sink, log, store)

    private fun launchedAt(at: String) = LoggedEvent(Instant.parse(at), null, Event.SessionStart(SessionEntry.LAUNCHER))

    @Test
    fun `the learned slot is saved`() = runTest {
        log.logged += (14..18).map { day -> launchedAt("2026-10-${day}T19:45:00Z") }

        val slot = learnDropTime()

        assertEquals(LocalTime.of(19, 30), slot.start)
        assertEquals(LocalTime.of(19, 30), store.learned.value)
    }

    @Test
    fun `events still waiting to be written are flushed first`() = runTest {
        log.logged += (14..17).map { day -> launchedAt("2026-10-${day}T19:45:00Z") }
        sink.append(launchedAt("2026-10-20T09:50:00Z"))

        learnDropTime()

        assertEquals(1, sink.flushCount)
    }

    @Test
    fun `the 14-day window starts at local midnight 13 days ago`() = runTest {
        log.logged += launchedAt("2026-10-06T23:59:59Z")
        log.logged += (7..10).map { day -> launchedAt("2026-10-${"%02d".format(day)}T07:00:00Z") }

        assertEquals(4, learnDropTime().sessionsCounted)
    }
}
