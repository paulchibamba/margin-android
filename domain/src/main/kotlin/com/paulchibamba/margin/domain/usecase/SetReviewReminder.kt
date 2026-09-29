package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.repository.SettingsRepository
import javax.inject.Inject

class SetReviewReminder @Inject constructor(private val settings: SettingsRepository) {

    suspend operator fun invoke(isOn: Boolean) {
        settings.setReviewReminder(isOn)
    }
}
