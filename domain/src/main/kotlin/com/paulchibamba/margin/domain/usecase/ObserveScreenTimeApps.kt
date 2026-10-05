package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.repository.Clock
import com.paulchibamba.margin.domain.repository.ScreenTimeStore
import com.paulchibamba.margin.domain.screentime.AppCategory
import com.paulchibamba.margin.domain.screentime.AppScreenTime
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ObserveScreenTimeApps @Inject constructor(private val clock: Clock, private val store: ScreenTimeStore) {

    operator fun invoke(): Flow<List<AppScreenTime>> {
        val today = clock.now().atZone(clock.zone()).toLocalDate()
        return store.observeTotalsFrom(today.minusDays(ScreenTimeReport.DAYS - 1)).map { apps ->
            apps.filter { app -> app.category != AppCategory.MARGIN }
        }
    }
}
