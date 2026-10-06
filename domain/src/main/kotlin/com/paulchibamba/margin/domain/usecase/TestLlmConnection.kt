package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.llm.LlmPurpose
import com.paulchibamba.margin.domain.llm.LlmReply
import com.paulchibamba.margin.domain.llm.LlmRequest
import com.paulchibamba.margin.domain.llm.LlmSchema
import javax.inject.Inject

class TestLlmConnection @Inject constructor(private val callLlm: CallLlm) {

    suspend operator fun invoke(): LlmReply = callLlm(REQUEST)

    private companion object {
        val SCHEMA = LlmSchema(
            name = "connection_test",
            json = """{"type":"object","properties":{"ok":{"type":"boolean"}},""" +
                """"required":["ok"],"additionalProperties":false}""",
        )

        val REQUEST = LlmRequest(
            purpose = LlmPurpose.CONNECTION_TEST,
            instructions = "This is a connection test. Reply with ok set to true.",
            input = "Are you there?",
            schema = SCHEMA,
            maxOutputTokens = 1_000,
        )
    }
}
