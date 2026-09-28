package com.paulchibamba.margin.feature.celebration

import com.paulchibamba.margin.domain.usecase.ConsumeNewBadges
import javax.inject.Inject

class CelebrationTrigger @Inject constructor(
    private val queue: CelebrationQueue,
    private val consumeNewBadges: ConsumeNewBadges,
) {
    fun onStreakSignal(isStreakExtended: Boolean) {
        if (isStreakExtended) queue.add(Celebration.StreakExtended)
    }

    suspend fun checkBadges() {
        consumeNewBadges().forEach { earned -> queue.add(Celebration.BadgeUnlocked(earned)) }
    }
}
