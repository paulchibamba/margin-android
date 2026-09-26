package com.paulchibamba.margin.data.pack

import kotlinx.serialization.json.Json

internal val PackJson = Json {
    ignoreUnknownKeys = true
    explicitNulls = false
}

internal fun decodePost(json: String): PostDto = PackJson.decodeFromString(PostDto.serializer(), json)
