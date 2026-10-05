package com.paulchibamba.margin.domain.progress.rules

import com.paulchibamba.margin.domain.progress.FactKey
import com.paulchibamba.margin.domain.progress.ProgressFixture
import com.paulchibamba.margin.domain.progress.RewardHistory
import com.paulchibamba.margin.domain.progress.RewardKind
import com.paulchibamba.margin.domain.progress.daysAgo
import com.paulchibamba.margin.domain.progress.leastPrivilege
import com.paulchibamba.margin.domain.progress.needToKnow
import com.paulchibamba.margin.domain.progress.pastReward
import com.paulchibamba.margin.domain.progress.privilegeInBrowsers
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CallbackRuleTest {
    private val fixture = ProgressFixture()
    private val rule = CallbackRule()

    @Test
    fun `a concept introduced today calls back to a related one from a week or more ago`() {
        fixture.introduce(leastPrivilege, on = daysAgo(10))
        fixture.introduce(needToKnow)

        val seed = fixture.seedsOf(rule).single()

        assertEquals(listOf(needToKnow.id, leastPrivilege.id), seed.conceptIds)
        assertEquals(leastPrivilege.title, seed.facts[FactKey.OLDER_CONCEPT])
        assertEquals("10", seed.facts[FactKey.DAYS_AGO])
    }

    @Test
    fun `a callback works across books`() {
        fixture.introduce(leastPrivilege, on = daysAgo(8))
        fixture.introduce(privilegeInBrowsers)

        assertEquals(privilegeInBrowsers.title, fixture.seedsOf(rule).single().facts[FactKey.CONCEPT])
    }

    @Test
    fun `a related concept from less than a week ago is no callback`() {
        fixture.introduce(leastPrivilege, on = daysAgo(6))
        fixture.introduce(needToKnow)

        assertTrue(fixture.seedsOf(rule).isEmpty())
    }

    @Test
    fun `a concept only glanced at today does not count as introduced for a callback`() {
        fixture.introduce(leastPrivilege, on = daysAgo(10))
        fixture.introduce(needToKnow)
        fixture.glance(needToKnow, times = 3, activeMs = 700)

        assertTrue(fixture.seedsOf(rule).isEmpty())
    }

    @Test
    fun `a callback for the same pair is made once`() {
        fixture.introduce(leastPrivilege, on = daysAgo(10))
        fixture.introduce(needToKnow)
        fixture.history = RewardHistory(listOf(pastReward(RewardKind.Callback, listOf(needToKnow, leastPrivilege))))

        assertTrue(fixture.seedsOf(rule).isEmpty())
    }
}
