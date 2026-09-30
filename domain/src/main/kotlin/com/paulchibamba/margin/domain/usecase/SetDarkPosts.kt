package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.repository.SettingsRepository
import javax.inject.Inject

class SetDarkPosts @Inject constructor(private val settings: SettingsRepository) {

    suspend operator fun invoke(isOn: Boolean) {
        settings.setDarkPosts(isOn)
    }
}
