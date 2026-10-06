package com.paulchibamba.margin.domain.drop

import com.paulchibamba.margin.domain.tracking.SessionEntry
import com.paulchibamba.margin.domain.tracking.SessionStarted
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DropTimeTest {
    private val zone = ZoneId.of("Africa/Lusaka")
    private val today = LocalDate.parse("2026-10-20")

    private fun startedAt(dateTime: String, entry: SessionEntry = SessionEntry.LAUNCHER) =
        SessionStarted(LocalDateTime.parse(dateTime).atZone(zone).toInstant(), entry)

    private fun learnedSlot(vararg starts: SessionStarted) = DropTime.learnedSlot(starts.toList(), today, zone)

    @Test
    fun `the drop slot is the half hour the app is most often opened in`() {
        val slot = learnedSlot(
            startedAt("2026-10-14T07:10"),
            startedAt("2026-10-15T21:05"),
            startedAt("2026-10-16T21:29"),
            startedAt("2026-10-17T21:00"),
            startedAt("2026-10-18T21:30"),
            startedAt("2026-10-19T12:45"),
        )

        assertEquals(LearnedSlot(LocalTime.of(21, 0), sessionsInSlot = 3, sessionsCounted = 6), slot)
        assertFalse(slot.isFallback)
    }

    @Test
    fun `sessions older than 14 days are not counted`() {
        val slot = learnedSlot(
            startedAt("2026-10-06T07:00"),
            startedAt("2026-10-06T23:50"),
            startedAt("2026-10-07T07:05"),
            startedAt("2026-10-08T07:10"),
            startedAt("2026-10-09T13:00"),
            startedAt("2026-10-19T13:20"),
            startedAt("2026-10-20T13:15"),
        )

        assertEquals(LearnedSlot(LocalTime.of(13, 0), sessionsInSlot = 3, sessionsCounted = 5), slot)
    }

    @Test
    fun `fewer than 5 sessions fall back to 08 30`() {
        val slot = learnedSlot(
            startedAt("2026-10-16T21:00"),
            startedAt("2026-10-17T21:00"),
            startedAt("2026-10-18T21:00"),
            startedAt("2026-10-19T21:00"),
        )

        assertEquals(LearnedSlot(LocalTime.of(8, 30), sessionsInSlot = 0, sessionsCounted = 4), slot)
        assertTrue(slot.isFallback)
    }

    @Test
    fun `sessions opened from the drop notification are ignored`() {
        val fromDrop = (14..19).map { day -> startedAt("2026-10-${day}T08:31", SessionEntry.DROP_NOTIFICATION) }
        val chosen = (15..19).map { day -> startedAt("2026-10-${day}T22:10") }

        val slot = DropTime.learnedSlot(fromDrop + chosen, today, zone)

        assertEquals(LearnedSlot(LocalTime.of(22, 0), sessionsInSlot = 5, sessionsCounted = 5), slot)
    }

    @Test
    fun `a tie goes to the earlier slot`() {
        val slot = learnedSlot(
            startedAt("2026-10-15T19:40"),
            startedAt("2026-10-16T19:35"),
            startedAt("2026-10-17T06:00"),
            startedAt("2026-10-18T06:20"),
            startedAt("2026-10-19T12:00"),
        )

        assertEquals(LocalTime.of(6, 0), slot.start)
    }

    @Test
    fun `slots follow the local clock, not UTC`() {
        val nearMidnight = (15..19).map { day -> startedAt("2026-10-${day}T00:15") }

        val slot = DropTime.learnedSlot(nearMidnight, today, zone)

        assertEquals(LocalTime.of(0, 0), slot.start)
    }
}
