package com.paulchibamba.margin.domain.progress.rules

import com.paulchibamba.margin.domain.progress.FactKey
import com.paulchibamba.margin.domain.progress.ProgressFixture
import com.paulchibamba.margin.domain.progress.RewardHistory
import com.paulchibamba.margin.domain.progress.RewardKind
import com.paulchibamba.margin.domain.progress.cia
import com.paulchibamba.margin.domain.progress.fundamentals
import com.paulchibamba.margin.domain.progress.leastPrivilege
import com.paulchibamba.margin.domain.progress.needToKnow
import com.paulchibamba.margin.domain.progress.noteIdOf
import com.paulchibamba.margin.domain.progress.pastReward
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ComingUpRuleTest {
    private val fixture = ProgressFixture().apply {
        readNotes = setOf(noteIdOf(fundamentals, 0), noteIdOf(fundamentals, 1), noteIdOf(fundamentals, 2))
    }
    private val rule = ComingUpRule()
    private val nextNote = noteIdOf(fundamentals, 3)

    @Test
    fun `a concept just past the frontier that relates to a known one is coming up`() {
        fixture.introduce(cia, leastPrivilege)

        val seed = fixture.seedsOf(rule).single { it.noteId == nextNote }

        assertEquals(listOf(needToKnow.id, leastPrivilege.id), seed.conceptIds)
        assertEquals(needToKnow.title, seed.facts[FactKey.CONCEPT])
        assertEquals(leastPrivilege.title, seed.facts[FactKey.KNOWN_CONCEPT])
        assertEquals("1", seed.facts[FactKey.NOTES_AWAY])
        assertEquals("Section 3", seed.facts[FactKey.SECTION])
    }

    @Test
    fun `nothing is coming up when no upcoming concept relates to a known one`() {
        fixture.introduce(cia)

        assertTrue(fixture.seedsOf(rule).isEmpty())
    }

    @Test
    fun `a coming up already made for that note and pair is not made again`() {
        fixture.introduce(leastPrivilege)
        val made = pastReward(RewardKind.ComingUp, listOf(needToKnow, leastPrivilege), note = nextNote)
        fixture.history = RewardHistory(listOf(made))

        assertTrue(fixture.seedsOf(rule).none { it.noteId == nextNote })
    }
}
