package com.paulchibamba.margin.domain.progress

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class FactsHashTest {

    @Test
    fun `the same facts give the same hash whatever the map order`() {
        val first = RewardSeed(RewardKind.Comeback, listOf(cia.id), facts = linkedMapOf("a" to "1", "b" to "2"))
        val second = RewardSeed(RewardKind.Comeback, listOf(cia.id), facts = linkedMapOf("b" to "2", "a" to "1"))

        assertEquals(first.factsHash, second.factsHash)
    }

    @Test
    fun `a different fact, kind or concept gives a different hash`() {
        val seed = RewardSeed(RewardKind.Comeback, listOf(cia.id), facts = mapOf("a" to "1"))

        assertNotEquals(seed.factsHash, seed.copy(facts = mapOf("a" to "2")).factsHash)
        assertNotEquals(seed.factsHash, seed.copy(kind = RewardKind.NowYouCan).factsHash)
        assertNotEquals(seed.factsHash, seed.copy(conceptIds = listOf(leastPrivilege.id)).factsHash)
    }

    @Test
    fun `facts that only differ in where a value is split give different hashes`() {
        val first = RewardSeed(RewardKind.Quote, emptyList(), facts = mapOf("ab" to "c"))
        val second = RewardSeed(RewardKind.Quote, emptyList(), facts = mapOf("a" to "bc"))

        assertNotEquals(first.factsHash, second.factsHash)
    }
}
