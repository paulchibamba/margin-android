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
    fun `a chapter title without a prefix is kept whole`() {
        assertEquals("Ch 4 · XSS", chapterLabel(4, "XSS"))
    }

    @Test
    fun `a chapter without a title is just its number`() {
        assertEquals("Ch 2", chapterLabel(2, "Chapter 2"))
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
    fun `one unlocked post is singular`() {
        assertEquals("unlocks 1 post", unlockedPostsLabel(1))
        assertEquals("unlocks 4 posts", unlockedPostsLabel(4))
    }
}
