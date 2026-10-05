package com.paulchibamba.margin.feature.settings.screentime

data class ScreenTimeSettingState(
    val hasAccess: Boolean = false,
    val dialog: ScreenTimeDialog? = null,
    val isDeleted: Boolean = false,
)
