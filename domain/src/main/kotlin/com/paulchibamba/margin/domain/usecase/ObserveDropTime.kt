package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.drop.DropTime
import com.paulchibamba.margin.domain.repository.DropTimeStore
import java.time.LocalTime
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class ObserveDropTime @Inject constructor(private val store: DropTimeStore) {

    operator fun invoke(): Flow<LocalTime> =
        store.observeLearned().map { learned -> learned ?: DropTime.FALLBACK }.distinctUntilChanged()
}
