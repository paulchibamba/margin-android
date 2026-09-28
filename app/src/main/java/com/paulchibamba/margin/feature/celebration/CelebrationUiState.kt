package com.paulchibamba.margin.feature.celebration

import com.paulchibamba.margin.domain.usecase.StreakSummary

data class CelebrationUiState(
    val celebration: Celebration? = null,
    val streak: StreakSummary? = null,
    val isLoaded: Boolean = false,
) {
    val isFinished: Boolean
        get() = isLoaded && celebration == null
}
