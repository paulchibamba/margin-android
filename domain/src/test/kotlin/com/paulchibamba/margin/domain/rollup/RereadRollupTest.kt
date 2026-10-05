package com.paulchibamba.margin.domain.rollup

import com.paulchibamba.margin.domain.rollup.RollupFixture.glancedNote
import com.paulchibamba.margin.domain.rollup.RollupFixture.leastPrivilege
import com.paulchibamba.margin.domain.rollup.RollupFixture.readNote
import kotlin.test.Test
import kotlin.test.assertEquals

class RereadRollupTest {
    private val rereads = RereadRollup.of(RollupFixture.day)

    @Test
    fun `revisits, reopened notes and scroll-backs are counted`() {
        assertEquals(1, rereads.revisits)
        assertEquals(1, rereads.notesReopened)
        assertEquals(2, rereads.scrollBacks)
    }

    @Test
    fun `hotspots add up every re-read of a concept or note, most first`() {
        val expected = listOf(
            RereadHotspot(RereadSubject.OfNote(readNote), 3),
            RereadHotspot(RereadSubject.OfConcept(leastPrivilege), 1),
            RereadHotspot(RereadSubject.OfNote(glancedNote), 1),
        )

        assertEquals(expected, rereads.hotspots)
    }
}
