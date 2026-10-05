package com.paulchibamba.margin.domain.progress.rules

import com.paulchibamba.margin.domain.feed.CandidateSource
import com.paulchibamba.margin.domain.model.Format
import com.paulchibamba.margin.domain.progress.FactKey
import com.paulchibamba.margin.domain.progress.ProgressFixture
import com.paulchibamba.margin.domain.progress.RewardHistory
import com.paulchibamba.margin.domain.progress.RewardKind
import com.paulchibamba.margin.domain.progress.cia
import com.paulchibamba.margin.domain.progress.defenceInDepth
import com.paulchibamba.margin.domain.progress.factPostOf
import com.paulchibamba.margin.domain.progress.fundamentals
import com.paulchibamba.margin.domain.progress.leastPrivilege
import com.paulchibamba.margin.domain.progress.needToKnow
import com.paulchibamba.margin.domain.progress.pastReward
import com.paulchibamba.margin.domain.tracking.Event
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ZoomOutRuleTest {
    private val fixture = ProgressFixture()
    private val rule = ZoomOutRule()

    private fun fundamentalsSeeds() = fixture.seedsOf(rule).filter { it.facts[FactKey.CHAPTER] == fundamentals.title }

    @Test
    fun `half of a chapter introduced makes a zoom out at 50 percent`() {
        fixture.introduce(cia, leastPrivilege)

        val seed = fundamentalsSeeds().single()

        assertEquals("50%", seed.facts[FactKey.THRESHOLD])
        assertEquals("2", seed.facts[FactKey.INTRODUCED])
        assertEquals("4", seed.facts[FactKey.TOTAL])
        assertEquals(needToKnow.title, seed.facts[FactKey.NEXT])
        assertEquals(listOf(cia.id, leastPrivilege.id), seed.conceptIds)
    }

    @Test
    fun `less than half of a chapter makes no zoom out`() {
        fixture.introduce(cia)

        assertTrue(fundamentalsSeeds().isEmpty())
    }

    @Test
    fun `a whole chapter makes a zoom out at 100 percent`() {
        fixture.introduce(cia, leastPrivilege, needToKnow, defenceInDepth)

        assertEquals("100%", fundamentalsSeeds().single().facts[FactKey.THRESHOLD])
    }

    @Test
    fun `each threshold fires at most once`() {
        fixture.introduce(cia, leastPrivilege, needToKnow)
        val done = mapOf(
            FactKey.BOOK to "Alice and Bob Learn Application Security",
            FactKey.CHAPTER to fundamentals.title,
            FactKey.THRESHOLD to "50%",
        )
        fixture.history = RewardHistory(listOf(pastReward(RewardKind.ZoomOut, facts = done)))

        assertTrue(fundamentalsSeeds().isEmpty())
    }

    @Test
    fun `a re-read hotspot makes a zoom out below the threshold`() {
        fixture.introduce(cia)
        val post = factPostOf(cia)
        fixture.log(Event.PostImpression(post.id, cia.id, Format.FACT, "Paper", CandidateSource.NEW, 3, false))
        repeat(2) { fixture.log(Event.PostRevisit(post.id, fromStep = 4, toStep = 3)) }

        assertEquals(cia.title, fundamentalsSeeds().single().facts[FactKey.REREAD_CONCEPT])
    }
}
