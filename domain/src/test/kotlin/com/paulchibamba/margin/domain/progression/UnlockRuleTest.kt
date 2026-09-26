package com.paulchibamba.margin.domain.progression

import com.paulchibamba.margin.domain.model.NotePosition
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class UnlockRuleTest {

    private val rule = UnlockRule(ReadingOnlyChapters(mapOf(appSec to setOf(2))))
    private val frontier = NotePosition(chapter = 3, order = 5)

    @Test
    fun `a concept at the frontier is unlocked`() {
        assertTrue(rule.isUnlocked(conceptAt(appSec, 3, 5), frontier))
    }

    @Test
    fun `a concept before the frontier is unlocked`() {
        assertTrue(rule.isUnlocked(conceptAt(appSec, 1, 40), frontier))
    }

    @Test
    fun `a concept past the frontier is locked`() {
        assertFalse(rule.isUnlocked(conceptAt(appSec, 3, 6), frontier))
    }

    @Test
    fun `nothing is unlocked before any reading`() {
        assertFalse(rule.isUnlocked(conceptAt(appSec, 1, 0), frontier = null))
    }

    @Test
    fun `a concept in a reading-only chapter is never unlocked`() {
        assertFalse(rule.isUnlocked(conceptAt(appSec, 2, 0), frontier))
    }

    @Test
    fun `a reading-only chapter in one book does not lock the same chapter in another`() {
        assertTrue(rule.isUnlocked(conceptAt(grokking, 2, 0), frontier))
    }
}
