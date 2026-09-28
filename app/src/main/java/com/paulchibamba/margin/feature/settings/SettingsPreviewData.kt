package com.paulchibamba.margin.feature.settings

import com.paulchibamba.margin.domain.model.BookSettings
import com.paulchibamba.margin.domain.model.BookSlug
import com.paulchibamba.margin.domain.model.Priority

object SettingsPreviewData {
    val settings = SettingsUiState(
        books = listOf(
            book("alice-bob-appsec", "Alice & Bob Learn AppSec", isActive = true, Priority.MAIN),
            book("tangled-web", "The Tangled Web", isActive = true, Priority.NORMAL),
            book("threat-modeling", "Threat Modeling", isActive = false, Priority.LOW),
        ),
        activeCount = 2,
        retention = 0.9,
        isLoading = false,
    )

    private fun book(slug: String, title: String, isActive: Boolean, priority: Priority) =
        BookSettingState(BookSettings(BookSlug(slug), isActive, priority), title, canToggle = true)
}
