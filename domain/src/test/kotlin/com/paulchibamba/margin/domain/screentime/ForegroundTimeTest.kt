package com.paulchibamba.margin.domain.screentime

import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

class ForegroundTimeTest {
    private val instagram = PackageName("com.instagram.android")
    private val youtube = PackageName("com.google.android.youtube")
    private val day = TimeWindow(at("00:00", day = 4), at("00:00", day = 5))

    private fun at(time: String, day: Int = 4): Instant = Instant.parse("2026-10-0${day}T$time:00Z")

    private fun resumed(packageName: PackageName, time: String, day: Int = 4) =
        UsageEvent.Resumed(at(time, day), packageName)

    private fun paused(packageName: PackageName, time: String, day: Int = 4) =
        UsageEvent.Paused(at(time, day), packageName)

    private fun foregroundOf(vararg events: UsageEvent, window: TimeWindow = day): Map<PackageName, Duration> =
        ForegroundTime.of(events.toList(), window)

    @Test
    fun `a resume and a pause count the time between them`() {
        val foreground = foregroundOf(resumed(instagram, "09:00"), paused(instagram, "09:25"))

        assertEquals(mapOf(instagram to 25.minutes), foreground)
    }

    @Test
    fun `separate visits to an app add up`() {
        val foreground = foregroundOf(
            resumed(instagram, "09:00"),
            paused(instagram, "09:10"),
            resumed(youtube, "09:10"),
            paused(youtube, "09:40"),
            resumed(instagram, "21:00"),
            paused(instagram, "21:05"),
        )

        assertEquals(mapOf(instagram to 15.minutes, youtube to 30.minutes), foreground)
    }

    @Test
    fun `moving between screens of the same app keeps counting`() {
        val foreground = foregroundOf(
            resumed(instagram, "09:00"),
            resumed(instagram, "09:05"),
            paused(instagram, "09:20"),
        )

        assertEquals(mapOf(instagram to 20.minutes), foreground)
    }

    @Test
    fun `a visit that started before midnight counts only from midnight`() {
        val foreground = foregroundOf(resumed(instagram, "23:50", day = 3), paused(instagram, "00:15"))

        assertEquals(mapOf(instagram to 15.minutes), foreground)
    }

    @Test
    fun `a visit that runs past midnight counts only until midnight`() {
        val foreground = foregroundOf(resumed(youtube, "23:40"), paused(youtube, "00:20", day = 5))

        assertEquals(mapOf(youtube to 20.minutes), foreground)
    }

    @Test
    fun `a visit that is still open counts until the end of the window`() {
        val today = TimeWindow(at("00:00"), at("10:00"))

        val foreground = foregroundOf(resumed(youtube, "09:30"), window = today)

        assertEquals(mapOf(youtube to 30.minutes), foreground)
    }

    @Test
    fun `an unpaired resume ends when another app comes to the front`() {
        val foreground = foregroundOf(resumed(instagram, "09:00"), resumed(youtube, "09:12"), paused(youtube, "09:20"))

        assertEquals(mapOf(instagram to 12.minutes, youtube to 8.minutes), foreground)
    }

    @Test
    fun `an unpaired resume ends when the screen turns off or the home screen opens`() {
        val foreground = foregroundOf(resumed(instagram, "09:00"), UsageEvent.NothingInFront(at("09:07")))

        assertEquals(mapOf(instagram to 7.minutes), foreground)
    }

    @Test
    fun `a pause from an app in the background is ignored`() {
        val foreground = foregroundOf(resumed(youtube, "09:00"), paused(instagram, "09:05"), paused(youtube, "09:30"))

        assertEquals(mapOf(youtube to 30.minutes), foreground)
    }

    @Test
    fun `visits entirely outside the window count nothing`() {
        val foreground = foregroundOf(
            resumed(instagram, "22:00", day = 3),
            paused(instagram, "22:30", day = 3),
            resumed(youtube, "00:10", day = 5),
        )

        assertEquals(emptyMap(), foreground)
    }

    @Test
    fun `events out of order are sorted by time first`() {
        val foreground = foregroundOf(paused(instagram, "09:25"), resumed(instagram, "09:00"))

        assertEquals(mapOf(instagram to 25.minutes), foreground)
    }
}
