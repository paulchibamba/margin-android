package com.paulchibamba.margin.domain.rollup

import com.paulchibamba.margin.domain.model.Format
import com.paulchibamba.margin.domain.tracking.SessionEntry
import java.time.ZoneOffset
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.Duration.Companion.minutes

class SessionRollupTest {
    private val sessions = SessionRollup.of(RollupFixture.day)

    @Test
    fun `sessions are counted by entry with their median length`() {
        assertEquals(2, sessions.sessions)
        assertEquals(7.minutes, sessions.medianSession)
        assertEquals(mapOf(SessionEntry.LAUNCHER to 1, SessionEntry.DUE_NOTIFICATION to 1), sessions.entries)
    }

    @Test
    fun `the format of the post that ended a session is an exit format`() {
        assertEquals(mapOf(Format.FACT to 1), sessions.exitFormats)
    }

    @Test
    fun `a day without sessions has no median`() {
        assertNull(SessionRollup.of(DayEvents(emptyList(), ZoneOffset.UTC)).medianSession)
    }
}
