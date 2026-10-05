package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.repository.Clock
import com.paulchibamba.margin.domain.repository.ScreenTimeStore
import javax.inject.Inject

class DeleteScreenTime @Inject constructor(
    private val clock: Clock,
    private val store: ScreenTimeStore,
    private val rollUp: RollUpEvents,
) {

    suspend operator fun invoke() {
        val dates = store.dates()
        store.deleteAll(ingestFrom = clock.now().atZone(clock.zone()).toLocalDate())
        rollUp.refreshScreenTime(dates)
    }
}
