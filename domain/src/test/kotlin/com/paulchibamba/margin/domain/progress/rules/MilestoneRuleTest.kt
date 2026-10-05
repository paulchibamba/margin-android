package com.paulchibamba.margin.domain.progress.rules

import com.paulchibamba.margin.domain.progress.FactKey
import com.paulchibamba.margin.domain.progress.ProgressFixture
import com.paulchibamba.margin.domain.progress.RewardHistory
import com.paulchibamba.margin.domain.progress.RewardKind
import com.paulchibamba.margin.domain.progress.appSec
import com.paulchibamba.margin.domain.progress.cia
import com.paulchibamba.margin.domain.progress.daysAgo
import com.paulchibamba.margin.domain.progress.defenceInDepth
import com.paulchibamba.margin.domain.progress.inputHandling
import com.paulchibamba.margin.domain.progress.leastPrivilege
import com.paulchibamba.margin.domain.progress.needToKnow
import com.paulchibamba.margin.domain.progress.parameterizedQueries
import com.paulchibamba.margin.domain.progress.pastReward
import com.paulchibamba.margin.domain.progress.sqlInjection
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MilestoneRuleTest {
    private val fixture = ProgressFixture()
    private val rule = MilestoneRule()

    @Test
    fun `a chapter with every idea met is a milestone, with the days it took`() {
        fixture.introduce(sqlInjection, on = daysAgo(4))
        fixture.introduce(parameterizedQueries, on = daysAgo(1))

        val seed = fixture.seedsOf(rule).single()

        assertEquals(MilestoneRule.SCOPE_CHAPTER, seed.facts[FactKey.SCOPE])
        assertEquals(inputHandling.title, seed.facts[FactKey.NAME])
        assertEquals(MilestoneRule.KIND_INTRODUCED, seed.facts[FactKey.MILESTONE])
        assertEquals("2", seed.facts[FactKey.INTRODUCED])
        assertEquals("5", seed.facts[FactKey.DAYS])
    }

    @Test
    fun `a chapter part met is no milestone`() {
        fixture.introduce(sqlInjection)

        assertTrue(fixture.seedsOf(rule).isEmpty())
    }

    @Test
    fun `a chapter fully remembered is a second milestone`() {
        fixture.remember(sqlInjection, parameterizedQueries)

        val kinds = fixture.seedsOf(rule).map { it.facts[FactKey.MILESTONE] }

        assertEquals(listOf(MilestoneRule.KIND_INTRODUCED, MilestoneRule.KIND_REMEMBERED), kinds)
    }

    @Test
    fun `a whole book met is a book milestone`() {
        fixture.introduce(cia, leastPrivilege, needToKnow, defenceInDepth, sqlInjection, parameterizedQueries)

        val book = fixture.seedsOf(rule).single { it.facts[FactKey.SCOPE] == MilestoneRule.SCOPE_BOOK }

        assertEquals(appSec.title, book.facts[FactKey.NAME])
        assertEquals("6", book.facts[FactKey.INTRODUCED])
    }

    @Test
    fun `a milestone is made once`() {
        fixture.introduce(sqlInjection, parameterizedQueries)
        val made = mapOf(
            FactKey.SCOPE to MilestoneRule.SCOPE_CHAPTER,
            FactKey.NAME to inputHandling.title,
            FactKey.BOOK to appSec.title,
            FactKey.MILESTONE to MilestoneRule.KIND_INTRODUCED,
        )
        fixture.history = RewardHistory(listOf(pastReward(RewardKind.Milestone, facts = made)))

        assertTrue(fixture.seedsOf(rule).isEmpty())
    }
}
