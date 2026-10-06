package com.paulchibamba.margin.domain.bake

import com.paulchibamba.margin.domain.llm.LlmPurpose
import com.paulchibamba.margin.domain.llm.LlmRequest
import com.paulchibamba.margin.domain.llm.ReasoningEffort
import com.paulchibamba.margin.domain.progress.RewardKind
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.add
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject

object RewardBatchRequest {
    private const val OUTPUT_TOKENS_PER_POST = 400
    private const val BASE_OUTPUT_TOKENS = 1_500

    fun of(seeds: List<BakeSeed>): LlmRequest = LlmRequest(
        purpose = LlmPurpose.BAKE,
        instructions = BakePrompts.REWARDS,
        input = buildJsonArray { seeds.forEach { seed -> add(inputOf(seed)) } }.toString(),
        schema = BakePrompts.REWARDS_SCHEMA,
        maxOutputTokens = BASE_OUTPUT_TOKENS + OUTPUT_TOKENS_PER_POST * seeds.size,
        reasoningEffort = ReasoningEffort.LOW,
    )

    private fun inputOf(bakeSeed: BakeSeed): JsonObject = buildJsonObject {
        val seed = bakeSeed.seed
        put("seedId", bakeSeed.id)
        put("kind", seed.kind.key)
        putJsonArray("conceptTitles") { bakeSeed.conceptTitles.forEach { title -> add(title) } }
        putJsonObject("facts") { seed.facts.toSortedMap().forEach { (key, value) -> put(key, value) } }
        if (seed.kind == RewardKind.Quote) put("excerpt", bakeSeed.sources.noteText?.let(Excerpts::quoteCandidates))
    }
}
