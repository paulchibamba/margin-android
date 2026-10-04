package com.paulchibamba.margin.data.tracking

import com.paulchibamba.margin.data.database.DatabaseTest
import com.paulchibamba.margin.data.database.entity.NoteReadEntity
import com.paulchibamba.margin.data.database.entity.PostSeenEntity
import com.paulchibamba.margin.domain.tracking.Event
import com.paulchibamba.margin.domain.tracking.EventFixtures
import com.paulchibamba.margin.domain.tracking.EventType
import com.paulchibamba.margin.domain.tracking.LoggedEvent
import com.paulchibamba.margin.domain.tracking.SessionCounts
import com.paulchibamba.margin.domain.tracking.SessionEndReason
import com.paulchibamba.margin.domain.tracking.SessionEntry
import com.paulchibamba.margin.domain.tracking.SessionId
import com.paulchibamba.margin.domain.tracking.UnfinishedSession
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.Duration.Companion.minutes
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class RoomEventLogTest : DatabaseTest() {
    private val log by lazy { RoomEventLog(database) }
    private val sink by lazy { RoomEventSink(database, TestScope()) }

    private suspend fun store(atMillis: Long, session: String, event: Event) {
        sink.append(LoggedEvent(Instant.ofEpochMilli(atMillis), SessionId(session), event))
        sink.flush()
    }

    @Test
    fun `an empty log has no unfinished session`() = runTest {
        assertNull(log.unfinishedSession())
    }

    @Test
    fun `the latest session without an end is unfinished, up to its last event`() = runTest {
        store(1_000, "a", Event.SessionStart(SessionEntry.LAUNCHER))
        store(2_000, "a", Event.SessionEnd(SessionEndReason.BACKGROUND, 1.minutes, 0, 0))
        store(5_000, "b", Event.SessionStart(SessionEntry.LAUNCHER))
        store(9_000, "b", EventFixtures.forType(EventType.POST_ACTION))

        val expected = UnfinishedSession(SessionId("b"), Instant.ofEpochMilli(5_000), Instant.ofEpochMilli(9_000))
        assertEquals(expected, log.unfinishedSession())
    }

    @Test
    fun `a finished latest session leaves nothing to close`() = runTest {
        store(1_000, "a", Event.SessionStart(SessionEntry.LAUNCHER))
        store(2_000, "a", Event.SessionEnd(SessionEndReason.IDLE, 1.minutes, 0, 0))

        assertNull(log.unfinishedSession())
    }

    @Test
    fun `recent events come newest first`() = runTest {
        store(1_000, "a", Event.SessionStart(SessionEntry.LAUNCHER))
        store(2_000, "a", EventFixtures.forType(EventType.SETTING_CHANGED))

        assertEquals(listOf("setting_changed", "session_start"), log.recent(limit = 5).map { it.typeKey })
    }

    @Test
    fun `a session counts the posts and notes seen inside its window`() = runTest {
        database.feedStateDao().upsertSeen(listOf(seen("p1", lastSeenAt = 1_500), seen("p2", lastSeenAt = 9_000)))
        database.readingDao().markNoteRead(NoteReadEntity("n1", readAt = 1_800))

        val counts = RoomSessionTally(database).countBetween(Instant.ofEpochMilli(1_000), Instant.ofEpochMilli(2_000))

        assertEquals(SessionCounts(posts = 1, notes = 1), counts)
    }

    private fun seen(postId: String, lastSeenAt: Long) = PostSeenEntity(
        postId = postId, firstSeenAt = lastSeenAt, lastSeenAt = lastSeenAt, lastSeenStep = 1, times = 1,
        lastDwellMs = null, lastEngagement = null, lastCorrect = null, lastSource = "new",
    )
}
