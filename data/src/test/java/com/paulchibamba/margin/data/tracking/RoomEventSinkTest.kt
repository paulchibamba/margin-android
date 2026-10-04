package com.paulchibamba.margin.data.tracking

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.paulchibamba.margin.data.database.MarginDatabase
import com.paulchibamba.margin.domain.tracking.Event
import com.paulchibamba.margin.domain.tracking.EventFixtures
import com.paulchibamba.margin.domain.tracking.EventType
import com.paulchibamba.margin.domain.tracking.LoggedEvent
import com.paulchibamba.margin.domain.tracking.SessionId
import java.time.Instant
import kotlin.test.assertEquals
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class RoomEventSinkTest {
    private val database = Room.inMemoryDatabaseBuilder(context(), MarginDatabase::class.java)
        .setQueryExecutor(Runnable::run)
        .setTransactionExecutor(Runnable::run)
        .allowMainThreadQueries()
        .build()
    private val flushScope = TestScope(StandardTestDispatcher())
    private val sink = RoomEventSink(database, flushScope)

    @After
    fun closeDatabase() = database.close()

    private fun context() = ApplicationProvider.getApplicationContext<android.content.Context>()

    private fun logged(index: Int, event: Event = EventFixtures.forType(EventType.POST_ACTION)) =
        LoggedEvent(Instant.ofEpochMilli(1_000L + index), SessionId("s1"), event)

    private suspend fun storedCount() = database.eventDao().latest(limit = 100).size

    @Test
    fun `nothing is written until the buffer holds 20 events`() = runTest {
        repeat(19) { index -> sink.append(logged(index)) }
        flushScope.testScheduler.advanceUntilIdle()
        assertEquals(0, storedCount())

        sink.append(logged(19))
        flushScope.testScheduler.advanceUntilIdle()

        assertEquals(20, storedCount())
    }

    @Test
    fun `a flush writes whatever is buffered, once`() = runTest {
        repeat(3) { index -> sink.append(logged(index)) }

        sink.flush()
        sink.flush()

        assertEquals(3, storedCount())
    }

    @Test
    fun `a stored event keeps its time, session, type, subject and props`() = runTest {
        sink.append(logged(0, EventFixtures.forType(EventType.NOTE_OPEN)))
        sink.flush()

        val stored = database.eventDao().latest(limit = 1).single()
        assertEquals(1_000L, stored.at)
        assertEquals("s1", stored.sessionId)
        assertEquals("note_open", stored.type)
        assertEquals(EventFixtures.note.value, stored.subjectId)
        assertEquals("""{"via":"feed_read","openCount":2}""", stored.props)
        assertEquals(EventMapper.SCHEMA_VERSION, stored.schemaVersion)
    }
}
