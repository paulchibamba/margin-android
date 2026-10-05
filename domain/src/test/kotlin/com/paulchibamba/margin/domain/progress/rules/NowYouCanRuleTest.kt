package com.paulchibamba.margin.domain.progress.rules

import com.paulchibamba.margin.domain.memory.Rating
import com.paulchibamba.margin.domain.progress.FactKey
import com.paulchibamba.margin.domain.progress.ProgressFixture
import com.paulchibamba.margin.domain.progress.RewardHistory
import com.paulchibamba.margin.domain.progress.RewardKind
import com.paulchibamba.margin.domain.progress.cia
import com.paulchibamba.margin.domain.progress.daysAgo
import com.paulchibamba.margin.domain.progress.leastPrivilege
import com.paulchibamba.margin.domain.progress.pastReward
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NowYouCanRuleTest {
    private val fixture = ProgressFixture()
    private val rule = NowYouCanRule()

    @Test
    fun `a concept newly remembered is a real win`() {
        fixture.remember(leastPrivilege, lastReview = daysAgo(1))

        val seed = fixture.seedsOf(rule).single()

        assertEquals(leastPrivilege.title, seed.facts[FactKey.CONCEPT])
        assertEquals(NowYouCanRule.WIN_REMEMBERED, seed.facts[FactKey.WIN])
    }

    @Test
    fun `a comeback is a real win`() {
        fixture.review(cia, Rating.AGAIN, daysAgo(6))
        fixture.review(cia, Rating.GOOD, daysAgo(3))
        fixture.review(cia, Rating.GOOD, daysAgo(1))

        assertEquals(NowYouCanRule.WIN_COMEBACK, fixture.seedsOf(rule).single().facts[FactKey.WIN])
    }

    @Test
    fun `without a new win there is nothing to say`() {
        fixture.remember(leastPrivilege, lastReview = daysAgo(3))
        fixture.history = RewardHistory(lastBakeAt = daysAgo(2))

        assertTrue(fixture.seedsOf(rule).isEmpty())
    }

    @Test
    fun `a concept is celebrated once`() {
        fixture.remember(leastPrivilege, lastReview = daysAgo(1))
        fixture.history = RewardHistory(listOf(pastReward(RewardKind.NowYouCan, listOf(leastPrivilege))))

        assertTrue(fixture.seedsOf(rule).isEmpty())
    }
}
