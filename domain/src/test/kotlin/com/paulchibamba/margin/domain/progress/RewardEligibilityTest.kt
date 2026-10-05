package com.paulchibamba.margin.domain.progress

import com.paulchibamba.margin.domain.memory.Rating
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RewardEligibilityTest {

    @Test
    fun `there is one rule per kind`() {
        val fixture = ProgressFixture().apply {
            introduce(cia, leastPrivilege, defenceInDepth, on = daysAgo(10))
            introduce(privilegeInBrowsers)
            readNotes = setOf(noteIdOf(fundamentals, 1))
            remember(sqlInjection, parameterizedQueries)
            lost(cia, NOW.minusSeconds(60))
            review(defenceInDepth, Rating.AGAIN, daysAgo(4))
            review(defenceInDepth, Rating.GOOD, daysAgo(2))
            review(defenceInDepth, Rating.GOOD, daysAgo(1))
        }

        val kinds = RewardEligibility().seedsFrom(fixture.facts(), fixture.history).map(RewardSeed::kind).toSet()

        assertEquals(RewardKind.all.toSet(), kinds)
    }

    @Test
    fun `the same seed from two rules is returned once`() {
        val seed = RewardSeed(RewardKind.Quote, emptyList(), facts = mapOf("a" to "b"))
        val rule = RewardRule { _, _ -> listOf(seed) }
        val eligibility = RewardEligibility(listOf(rule, rule))

        assertEquals(listOf(seed), eligibility.seedsFrom(ProgressFixture().facts(), RewardHistory.Empty))
    }

    @Test
    fun `the progress package has no Android or Room imports`() {
        val progressPackage = File("src/main/kotlin/com/paulchibamba/margin/domain/progress")
        val sources = progressPackage.walk().filter { it.isFile }.toList()

        assertTrue(sources.isNotEmpty())
        assertTrue(sources.none { file -> file.readLines().any { it.startsWith("import android") } })
    }
}
