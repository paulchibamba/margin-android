package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.llm.LlmSettings
import com.paulchibamba.margin.domain.repository.LlmSettingsRepository
import javax.inject.Inject

class UpdateLlmSettings @Inject constructor(private val settings: LlmSettingsRepository) {

    suspend operator fun invoke(update: LlmSettings) {
        settings.saveSettings(update)
    }
}
