package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.memory.DesiredRetention
import com.paulchibamba.margin.domain.repository.SettingsRepository
import javax.inject.Inject

class SetDesiredRetention @Inject constructor(private val settings: SettingsRepository) {

    suspend operator fun invoke(retention: Double) {
        settings.setDesiredRetention(DesiredRetention.normalise(retention))
    }
}
