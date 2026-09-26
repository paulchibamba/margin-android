package com.paulchibamba.margin.domain.feed.ranking

import kotlin.random.Random

class CandidatePicker(private val epsilon: Double, private val random: Random) {

    fun pick(ranked: List<ScoredCandidate>): Pick {
        require(ranked.isNotEmpty()) { "Cannot pick from no candidates" }
        val explore = random.nextDouble() < epsilon && ranked.size > 1
        if (!explore) return Pick(ranked.first(), rank = 1, wasExploration = false)
        val index = 1 + random.nextInt(ranked.size - 1)
        return Pick(ranked[index], rank = index + 1, wasExploration = true)
    }
}
