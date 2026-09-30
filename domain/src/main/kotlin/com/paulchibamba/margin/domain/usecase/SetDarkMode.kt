package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.model.DarkMode
import com.paulchibamba.margin.domain.repository.SettingsRepository
import javax.inject.Inject

class SetDarkMode @Inject constructor(private val settings: SettingsRepository) {

    suspend operator fun invoke(mode: DarkMode) {
        settings.setDarkMode(mode)
    }
}
