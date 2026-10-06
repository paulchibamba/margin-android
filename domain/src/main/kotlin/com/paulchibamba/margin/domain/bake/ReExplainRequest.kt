package com.paulchibamba.margin.domain.bake

import com.paulchibamba.margin.domain.llm.LlmPurpose
import com.paulchibamba.margin.domain.llm.LlmRequest
import com.paulchibamba.margin.domain.llm.ReasoningEffort
import com.paulchibamba.margin.domain.progress.FactKey
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

object ReExplainRequest {
    private const val MAX_OUTPUT_TOKENS = 2_000

    fun of(bakeSeed: BakeSeed): LlmRequest {
        val facts = bakeSeed.seed.facts
        val input = buildJsonObject {
            put("concept", facts[FactKey.CONCEPT])
            put("summary", facts[FactKey.SUMMARY])
            put("excerpt", bakeSeed.sources.noteText?.let(Excerpts::opening).orEmpty())
            put("anglesShown", facts[FactKey.ANGLES_SHOWN].orEmpty())
            put("anchorConcept", facts[FactKey.ANCHOR])
            put("trigger", facts[FactKey.TRIGGER])
        }
        return LlmRequest(
            purpose = LlmPurpose.EXPLAIN,
            instructions = BakePrompts.RE_EXPLAIN,
            input = input.toString(),
            schema = BakePrompts.RE_EXPLAIN_SCHEMA,
            maxOutputTokens = MAX_OUTPUT_TOKENS,
            reasoningEffort = ReasoningEffort.LOW,
        )
    }
}
