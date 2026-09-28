package com.paulchibamba.margin.feature.settings

import com.paulchibamba.margin.domain.model.BookSettings

data class BookSettingState(val settings: BookSettings, val title: String, val canToggle: Boolean)
