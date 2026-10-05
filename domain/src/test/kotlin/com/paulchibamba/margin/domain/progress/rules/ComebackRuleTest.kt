package com.paulchibamba.margin.domain.progress.rules

import com.paulchibamba.margin.domain.memory.Rating
import com.paulchibamba.margin.domain.progress.FactKey
import com.paulchibamba.margin.domain.progress.ProgressFixture
import com.paulchibamba.margin.domain.progress.RewardHistory
import com.paulchibamba.margin.domain.progress.RewardKind
import com.paulchibamba.margin.domain.progress.cia
import com.paulchibamba.margin.domain.progress.daysAgo
import com.paulchibamba.margin.domain.progress.pastReward
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ComebackRuleTest {
    private val fixture = ProgressFixture()
    private val rule = ComebackRule()

    private fun failTwiceThenPass(passes: Int) {
        fixture.review(cia, Rating.AGAIN, daysAgo(10))
        fixture.review(cia, Rating.AGAIN, daysAgo(9))
        repeat(passes) { day -> fixture.review(cia, Rating.GOOD, daysAgo(5L - day)) }
    }

    @Test
    fun `fails then two passes make a comeback with its date and counts`() {
        failTwiceThenPass(passes = 3)

        val seed = fixture.seedsOf(rule).single()

        assertEquals(
            mapOf(
                FactKey.CONCEPT to cia.title,
                FactKey.DATE to "25 Sep",
                FactKey.FAIL_COUNT to "2",
                FactKey.PASS_COUNT to "3",
            ),
            seed.facts,
        )
    }

    @Test
    fun `fails then one pass make no comeback`() {
        failTwiceThenPass(passes = 1)

        assertTrue(fixture.seedsOf(rule).isEmpty())
    }

    @Test
    fun `a comeback already made for that fail is not made again`() {
        failTwiceThenPass(passes = 2)
        val made = pastReward(RewardKind.Comeback, listOf(cia), facts = mapOf(FactKey.DATE to "25 Sep"))
        fixture.history = RewardHistory(listOf(made))

        assertTrue(fixture.seedsOf(rule).isEmpty())
    }
}
