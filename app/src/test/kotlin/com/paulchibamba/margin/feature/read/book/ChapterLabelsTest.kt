package com.paulchibamba.margin.feature.read.book

import com.paulchibamba.margin.domain.feed.appSec
import com.paulchibamba.margin.domain.model.ChapterRef
import com.paulchibamba.margin.domain.usecase.NoteTally
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

class ChapterLabelsTest {

    @Test
    fun `a reading-only chapter shows its tag and notes read`() {
        val readingOnly = row(NoteTally(6, 6, Duration.ZERO), isReadingOnly = true)

        assertEquals("Reading only · 6/6", chapterDetailLabel(readingOnly))
    }

    @Test
    fun `a finished chapter shows its notes`() {
        assertEquals("33/33 notes", chapterDetailLabel(row(NoteTally(33, 33, Duration.ZERO))))
    }

    @Test
    fun `a chapter marked known says so`() {
        assertEquals("Known · 0/28", chapterDetailLabel(row(NoteTally(0, 28, 25.minutes), isKnown = true)))
    }

    @Test
    fun `the current chapter shows the time left`() {
        assertEquals("5/8 · 3 min left", chapterDetailLabel(row(NoteTally(5, 8, 3.minutes), isCurrent = true)))
    }

    @Test
    fun `a chapter ahead shows its reading time`() {
        assertEquals("0/41 · 38 min", chapterDetailLabel(row(NoteTally(0, 41, 38.minutes))))
    }

    private fun row(
        tally: NoteTally,
        isReadingOnly: Boolean = false,
        isKnown: Boolean = false,
        isCurrent: Boolean = false,
    ) = ChapterRowState(
        chapter = ChapterRef(appSec.slug, 1),
        title = "Security basics",
        tally = tally,
        isReadingOnly = isReadingOnly,
        isKnown = isKnown,
        isDone = isKnown || tally.isFinished,
        isCurrent = isCurrent,
        isAhead = false,
        nextNote = null,
    )
}
