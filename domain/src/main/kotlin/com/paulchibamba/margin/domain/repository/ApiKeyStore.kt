package com.paulchibamba.margin.domain.repository

import com.paulchibamba.margin.domain.llm.ApiKey
import kotlinx.coroutines.flow.Flow

interface ApiKeyStore {
    suspend fun load(): ApiKey?
    fun observe(): Flow<ApiKey?>
    suspend fun save(key: ApiKey)
    suspend fun clear()
}
