package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.repository.Clock
import com.paulchibamba.margin.domain.repository.EventLog
import javax.inject.Inject
import kotlin.time.Duration.Companion.days
import kotlin.time.toJavaDuration

class PruneEventLog @Inject constructor(
    private val clock: Clock,
    private val log: EventLog,
    private val rollUp: RollUpEvents,
) {

    suspend operator fun invoke(): Int {
        rollUp.missedDays()
        return log.deleteBefore(clock.now().minus(RETENTION.toJavaDuration()))
    }

    companion object {
        val RETENTION = 180.days
    }
}
