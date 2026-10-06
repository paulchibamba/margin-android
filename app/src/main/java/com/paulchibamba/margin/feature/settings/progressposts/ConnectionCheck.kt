package com.paulchibamba.margin.feature.settings.progressposts

import com.paulchibamba.margin.domain.llm.LlmReply

sealed interface ConnectionCheck {

    data object Idle : ConnectionCheck

    data object Running : ConnectionCheck

    data class Done(val reply: LlmReply) : ConnectionCheck
}
