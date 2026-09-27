package com.paulchibamba.margin.feature.feed

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class FeedLabelsTest {

    @Test
    fun `a chapter label drops the book's own chapter prefix`() {
        assertEquals("Ch 1 · Foundations", chapterLabel(1, "CHAPTER 1: Foundations"))
    }

    @Test
    fun `a bare chapter number before the title is dropped`() {
        assertEquals("Ch 1 · Know your enemy", chapterLabel(1, "1 Know your enemy"))
    }

    @Test
    fun `a title that starts with a number is not a chapter number`() {
        assertEquals("Ch 5 · 2FA in practice", chapterLabel(5, "2FA in practice"))
    }

    @Test
    fun `a chapter title without a prefix is kept whole`() {
        assertEquals("Ch 4 · XSS", chapterLabel(4, "XSS"))
    }

    @Test
    fun `a chapter without a title is just its number`() {
        assertEquals("Ch 2", chapterLabel(2, "Chapter 2"))
    }

    @Test
    fun `the unlock label names the whole minutes of reading left`() {
        assertEquals("Read to unlock · 3 min", readToUnlockLabel(150.seconds))
    }

    @Test
    fun `one note ahead is singular`() {
        assertEquals("1 note ahead", notesAheadLabel(1))
        assertEquals("3 notes ahead", notesAheadLabel(3))
    }

    @Test
    fun `reading time rounds up to whole minutes, at least one`() {
        assertEquals("~1 min", readingTimeLabel(40.seconds))
        assertEquals("~2 min", readingTimeLabel(70.seconds))
    }

    @Test
    fun `the next review time uses the largest whole unit`() {
        assertEquals("in under a minute", dueInLabel(30.seconds))
        assertEquals("in 45 min", dueInLabel(45.minutes))
        assertEquals("in 2 h", dueInLabel(2.hours + 10.minutes))
        assertEquals("in 3 d", dueInLabel(3.days))
    }

    @Test
    fun `the next note spells out how many posts it unlocks`() {
        assertEquals("~1 min · unlocks 1 post", nextNoteDetail(1.minutes, unlockedPosts = 1))
        assertEquals("~1 min · unlocks 4 posts", nextNoteDetail(1.minutes, unlockedPosts = 4))
    }

    @Test
    fun `a next note that unlocks nothing only shows its reading time`() {
        assertEquals("~1 min", nextNoteDetail(1.minutes, unlockedPosts = 0))
    }

    @Test
    fun `when a concept comes back reads in minutes, hours or days`() {
        assertEquals("~10 min", approximateIntervalLabel(10.minutes))
        assertEquals("~5 h", approximateIntervalLabel(5.hours))
        assertEquals("~1 day", approximateIntervalLabel(1.days))
        assertEquals("~6 days", approximateIntervalLabel(6.days))
    }

    @Test
    fun `a grade interval under a minute still reads as a minute`() {
        assertEquals("~1 min", approximateIntervalLabel(20.seconds))
        assertEquals("1m", shortIntervalLabel(20.seconds))
    }

    @Test
    fun `grade button intervals are short`() {
        assertEquals("10m", shortIntervalLabel(10.minutes))
        assertEquals("3h", shortIntervalLabel(3.hours))
        assertEquals("2d", shortIntervalLabel(2.days))
    }
}
