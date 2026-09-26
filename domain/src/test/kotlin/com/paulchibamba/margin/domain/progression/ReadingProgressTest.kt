package com.paulchibamba.margin.domain.progression

import com.paulchibamba.margin.domain.model.NotePosition
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ReadingProgressTest {

    @Test
    fun `a book with nothing read has no frontier`() {
        assertNull(ReadingProgress.NothingRead.frontierOf(appSec))
    }

    @Test
    fun `the frontier is the furthest note read, across chapters`() {
        val progress = ReadingProgress(
            readNotes = setOf(noteIn(appSec, 1, 30), noteIn(appSec, 2, 4), noteIn(appSec, 1, 2)),
            knownChapterEnds = emptySet(),
        )

        assertEquals(NotePosition(chapter = 2, order = 4), progress.frontierOf(appSec))
    }

    @Test
    fun `a known chapter moves the frontier to its last note`() {
        val progress = ReadingProgress(
            readNotes = setOf(noteIn(appSec, 1, 3)),
            knownChapterEnds = setOf(noteIn(appSec, 3, 41)),
        )

        assertEquals(NotePosition(chapter = 3, order = 41), progress.frontierOf(appSec))
    }

    @Test
    fun `notes read in other books do not move the frontier`() {
        val progress = ReadingProgress(
            readNotes = setOf(noteIn(appSec, 1, 3), noteIn(grokking, 9, 9)),
            knownChapterEnds = setOf(noteIn(grokking, 12, 20)),
        )

        assertEquals(NotePosition(chapter = 1, order = 3), progress.frontierOf(appSec))
    }
}
