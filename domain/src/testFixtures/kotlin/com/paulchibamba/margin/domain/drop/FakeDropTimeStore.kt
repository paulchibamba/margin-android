package com.paulchibamba.margin.domain.drop

import com.paulchibamba.margin.domain.repository.DropTimeStore
import java.time.LocalTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeDropTimeStore : DropTimeStore {
    val learned = MutableStateFlow<LocalTime?>(null)

    override fun observeLearned(): Flow<LocalTime?> = learned

    override suspend fun saveLearned(slot: LocalTime) {
        learned.value = slot
    }
}
