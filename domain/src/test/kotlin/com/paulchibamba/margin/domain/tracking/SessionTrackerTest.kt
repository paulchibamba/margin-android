package com.paulchibamba.margin.domain.tracking

import com.paulchibamba.margin.domain.usecase.FixedClock
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration
import kotlinx.coroutines.test.runTest

class SessionTrackerTest {
    private val clock = FixedClock()
    private val sink = RecordingEventSink()
    private val log = FakeEventLog()
    private var nextId = 0
    private val tracker = SessionTracker(
        clock = clock,
        sink = sink,
        tally = { _, _ -> SessionCounts(posts = 7, notes = 1) },
        log = log,
        sessionIds = { SessionId("s${++nextId}") },
    )

    private fun advance(by: Duration) {
        clock.instant = clock.instant + by.toJavaDuration()
    }

    private fun events() = sink.all.map(LoggedEvent::event)

    private inline fun <reified T : Event> only(): List<T> = events().filterIsInstance<T>()

    @Test
    fun `coming to the foreground starts a session with its entry`() = runTest {
        tracker.onForeground(SessionEntry.DUE_NOTIFICATION)

        assertEquals(listOf(Event.SessionStart(SessionEntry.DUE_NOTIFICATION)), events())
        assertEquals(SessionId("s1"), sink.all.single().sessionId)
    }

    @Test
    fun `coming back within 30 seconds keeps the same session`() = runTest {
        tracker.onForeground(SessionEntry.LAUNCHER)
        tracker.onBackground(isScreenOn = true)
        advance(29.seconds)
        tracker.endIfAway()
        tracker.onForeground(SessionEntry.LAUNCHER)

        assertEquals(1, only<Event.SessionStart>().size)
        assertTrue(only<Event.SessionEnd>().isEmpty())
    }

    @Test
    fun `30 seconds away ends the session at the moment the user left`() = runTest {
        tracker.onForeground(SessionEntry.LAUNCHER)
        advance(2.minutes)
        val leftAt = clock.instant
        tracker.onBackground(isScreenOn = true)
        advance(30.seconds)
        tracker.endIfAway()

        val end = sink.all.single { it.event is Event.SessionEnd }
        assertEquals(leftAt, end.at)
        assertEquals(Event.SessionEnd(SessionEndReason.BACKGROUND, 2.minutes, posts = 7, notes = 1), end.event)
    }

    @Test
    fun `returning after a long absence closes the old session and starts a new one`() = runTest {
        tracker.onForeground(SessionEntry.LAUNCHER)
        tracker.onBackground(isScreenOn = true)
        advance(3.minutes)
        tracker.onForeground(SessionEntry.DROP_NOTIFICATION)

        val starts = sink.all.filter { it.event is Event.SessionStart }
        assertEquals(listOf("s1", "s2"), starts.map { it.sessionId?.value })
        assertEquals(SessionEntry.DROP_NOTIFICATION, (starts.last().event as Event.SessionStart).entry)
        assertEquals(SessionId("s1"), sink.all.single { it.event is Event.SessionEnd }.sessionId)
    }

    @Test
    fun `turning the screen off ends the session with that reason`() = runTest {
        tracker.onForeground(SessionEntry.LAUNCHER)
        tracker.onBackground(isScreenOn = false)
        advance(1.minutes)
        tracker.endIfAway()

        assertEquals(SessionEndReason.SCREEN_OFF, only<Event.SessionEnd>().single().reason)
    }

    @Test
    fun `five minutes without input ends the session as idle`() = runTest {
        tracker.onForeground(SessionEntry.LAUNCHER)
        advance(4.minutes)
        tracker.endIfIdle()
        assertTrue(only<Event.SessionEnd>().isEmpty())

        advance(1.minutes)
        tracker.endIfIdle()

        assertEquals(SessionEndReason.IDLE, only<Event.SessionEnd>().single().reason)
    }

    @Test
    fun `input keeps the session alive`() = runTest {
        tracker.onForeground(SessionEntry.LAUNCHER)
        advance(4.minutes)
        tracker.onInput()
        advance(4.minutes)
        tracker.endIfIdle()

        assertTrue(only<Event.SessionEnd>().isEmpty())
    }

    @Test
    fun `touching the screen after an idle end starts a resumed session`() = runTest {
        tracker.onForeground(SessionEntry.LAUNCHER)
        advance(6.minutes)
        tracker.endIfIdle()
        tracker.onInput()

        assertEquals(SessionEntry.RESUME, only<Event.SessionStart>().last().entry)
    }

    @Test
    fun `a session left open by the last run is closed once at its last event`() = runTest {
        val startedAt = Instant.parse("2026-09-30T21:00:00Z")
        val lastEventAt = Instant.parse("2026-09-30T21:04:00Z")
        log.unfinished = UnfinishedSession(SessionId("old"), startedAt, lastEventAt)

        tracker.onForeground(SessionEntry.LAUNCHER)
        tracker.onBackground(isScreenOn = true)
        tracker.onForeground(SessionEntry.LAUNCHER)

        val end = sink.all.single { it.event is Event.SessionEnd }
        assertEquals(SessionId("old"), end.sessionId)
        assertEquals(lastEventAt, end.at)
        assertEquals(Event.SessionEnd(SessionEndReason.UNKNOWN, 4.minutes, 7, 1), end.event)
        assertIs<Event.SessionEnd>(events().first())
    }

    @Test
    fun `recorded events carry the open session and the time`() = runTest {
        tracker.record(EventFixtures.forType(EventType.SETTING_CHANGED))
        tracker.onForeground(SessionEntry.LAUNCHER)
        advance(5.seconds)
        tracker.record(EventFixtures.forType(EventType.SETTING_CHANGED))

        val (outside, inside) = sink.all.filter { it.event is Event.SettingChanged }
        assertNull(outside.sessionId)
        assertEquals(SessionId("s1"), inside.sessionId)
        assertEquals(clock.instant, inside.at)
    }

    @Test
    fun `listeners add their events right after a session starts and right before it ends`() = runTest {
        val opened = Event.SettingChanged("opened", null, "yes")
        val closed = Event.SettingChanged("closed", null, "yes")
        tracker.addListener(object : SessionListener {
            override fun eventsAtStart() = listOf(opened)
            override fun eventsAtEnd() = listOf(closed)
        })

        tracker.onForeground(SessionEntry.LAUNCHER)
        advance(6.minutes)
        tracker.endIfIdle()

        val order = listOf("session_start", "setting_changed", "setting_changed", "session_end")
        assertEquals(order, events().map { it.type.key })
        assertEquals(listOf(opened, closed), only<Event.SettingChanged>())
        assertTrue(sink.all.all { it.sessionId == SessionId("s1") })
    }

    @Test
    fun `going to the background and ending a session flush the buffer`() = runTest {
        tracker.onForeground(SessionEntry.LAUNCHER)
        tracker.onBackground(isScreenOn = true)
        assertEquals(1, sink.flushCount)

        advance(1.minutes)
        tracker.endIfAway()

        assertEquals(2, sink.flushCount)
        assertTrue(sink.pending.isEmpty())
    }

    @Test
    fun `end listeners hear about each session end once the events are flushed`() = runTest {
        var ends = 0
        tracker.addEndListener { ends += 1 }

        tracker.onForeground(SessionEntry.LAUNCHER)
        tracker.onBackground(isScreenOn = false)
        assertEquals(0, ends)
        advance(1.minutes)
        tracker.endIfAway()

        assertEquals(1, ends)
        assertTrue(sink.pending.isEmpty())
    }
}
