package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.model.BookSettings
import com.paulchibamba.margin.domain.progression.ActiveBookLimit
import com.paulchibamba.margin.domain.repository.SettingsRepository
import javax.inject.Inject

class UpdateBookSettings @Inject constructor(private val settings: SettingsRepository) {

    suspend operator fun invoke(update: BookSettings): Boolean {
        val others = settings.bookSettings().filterNot { it.bookSlug == update.bookSlug }
        val activeCount = others.count(BookSettings::isActive) + if (update.isActive) 1 else 0
        if (!ActiveBookLimit.allows(activeCount)) return false
        settings.saveBookSettings(update)
        return true
    }
}
