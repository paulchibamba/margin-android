package com.paulchibamba.margin.domain.progress.rules

import com.paulchibamba.margin.domain.progress.FactKey
import com.paulchibamba.margin.domain.progress.ProgressFixture
import com.paulchibamba.margin.domain.progress.RewardHistory
import com.paulchibamba.margin.domain.progress.RewardKind
import com.paulchibamba.margin.domain.progress.cia
import com.paulchibamba.margin.domain.progress.fundamentals
import com.paulchibamba.margin.domain.progress.noteIdOf
import com.paulchibamba.margin.domain.progress.pastReward
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class QuoteRuleTest {
    private val fixture = ProgressFixture().apply { readNotes = setOf(noteIdOf(fundamentals, 1)) }
    private val rule = QuoteRule()

    @Test
    fun `a read note can be quoted, citing its book and section`() {
        val seed = fixture.seedsOf(rule).single()

        assertEquals(noteIdOf(fundamentals, 1), seed.noteId)
        assertEquals(listOf(cia.id), seed.conceptIds)
        assertEquals("Section 1", seed.facts[FactKey.SECTION])
    }

    @Test
    fun `a note already quoted is not quoted again`() {
        fixture.history = RewardHistory(listOf(pastReward(RewardKind.Quote, note = noteIdOf(fundamentals, 1))))

        assertTrue(fixture.seedsOf(rule).isEmpty())
    }

    @Test
    fun `nothing read means nothing to quote`() {
        fixture.readNotes = emptySet()

        assertTrue(fixture.seedsOf(rule).isEmpty())
    }
}
