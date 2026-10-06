package com.paulchibamba.margin.domain.llm

import com.paulchibamba.margin.domain.repository.ApiKeyStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeApiKeyStore(key: ApiKey? = null) : ApiKeyStore {
    private val stored = MutableStateFlow(key)

    val key: ApiKey? get() = stored.value

    override suspend fun load(): ApiKey? = stored.value

    override fun observe(): Flow<ApiKey?> = stored

    override suspend fun save(key: ApiKey) {
        stored.value = key
    }

    override suspend fun clear() {
        stored.value = null
    }
}
