package com.paulchibamba.margin.feature.celebration

import com.paulchibamba.margin.domain.rewards.EarnedBadge

sealed interface Celebration {

    data object StreakExtended : Celebration

    data class BadgeUnlocked(val earned: EarnedBadge) : Celebration
}
