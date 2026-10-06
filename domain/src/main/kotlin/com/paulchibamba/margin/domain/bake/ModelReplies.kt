package com.paulchibamba.margin.domain.bake

import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

object ModelReplies {
    private val json = Json { ignoreUnknownKeys = true }

    fun rewardBatch(text: String): ModelRewardBatch? = decode { json.decodeFromString<ModelRewardBatch>(text) }

    fun reExplain(text: String): ModelReExplain? = decode { json.decodeFromString<ModelReExplain>(text) }

    private fun <T> decode(read: () -> T): T? = try {
        read()
    } catch (_: SerializationException) {
        null
    } catch (_: IllegalArgumentException) {
        null
    }
}
