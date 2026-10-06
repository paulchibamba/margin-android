package com.paulchibamba.margin.domain.llm

import com.paulchibamba.margin.domain.repository.LlmSettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeLlmSettingsRepository(settings: LlmSettings = LlmSettings()) : LlmSettingsRepository {
    private val stored = MutableStateFlow(settings)

    override suspend fun settings(): LlmSettings = stored.value

    override fun observeSettings(): Flow<LlmSettings> = stored

    override suspend fun saveSettings(settings: LlmSettings) {
        stored.value = settings
    }
}
