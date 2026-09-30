package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.model.DarkMode
import com.paulchibamba.margin.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveDarkMode @Inject constructor(private val settings: SettingsRepository) {

    operator fun invoke(): Flow<DarkMode> = settings.observeDarkMode()
}
