package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class ObserveAppearance @Inject constructor(private val settings: SettingsRepository) {

    operator fun invoke(): Flow<Appearance> =
        combine(settings.observeDarkMode(), settings.observeDarkPosts(), ::Appearance)
}
