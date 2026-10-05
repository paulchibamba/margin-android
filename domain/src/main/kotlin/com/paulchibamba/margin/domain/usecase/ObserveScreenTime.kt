package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.repository.Clock
import com.paulchibamba.margin.domain.repository.RollupStore
import com.paulchibamba.margin.domain.rollup.DailyRollup
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ObserveScreenTime @Inject constructor(private val store: RollupStore, private val clock: Clock) {

    operator fun invoke(): Flow<ScreenTimeReport?> {
        val today = clock.now().atZone(clock.zone()).toLocalDate()
        return store.observeFrom(today.minusDays(ScreenTimeReport.DAYS - 1)).map { rollups ->
            rollups.mapNotNull(::dayOf).takeIf(List<ScreenTimeDay>::isNotEmpty)?.let(::ScreenTimeReport)
        }
    }

    private fun dayOf(rollup: DailyRollup): ScreenTimeDay? = rollup.metrics.screenTime?.let { screenTime ->
        ScreenTimeDay(rollup.date, screenTime.margin, screenTime.doom)
    }
}
