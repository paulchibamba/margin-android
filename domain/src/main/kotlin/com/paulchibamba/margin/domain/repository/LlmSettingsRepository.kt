package com.paulchibamba.margin.domain.repository

import com.paulchibamba.margin.domain.llm.LlmSettings
import kotlinx.coroutines.flow.Flow

interface LlmSettingsRepository {
    suspend fun settings(): LlmSettings
    fun observeSettings(): Flow<LlmSettings>
    suspend fun saveSettings(settings: LlmSettings)
}
