package com.paulchibamba.margin.feature.settings.screentime

import com.paulchibamba.margin.domain.screentime.AppScreenTime

data class DoomAppsUiState(val apps: List<AppScreenTime> = emptyList(), val isLoading: Boolean = true)
