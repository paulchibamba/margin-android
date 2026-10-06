package com.paulchibamba.margin.data.drop

import com.paulchibamba.margin.data.database.MarginDatabase
import com.paulchibamba.margin.data.database.MetaKey
import com.paulchibamba.margin.data.database.entity.MetaEntity
import com.paulchibamba.margin.domain.repository.DropTimeStore
import java.time.LocalTime
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomDropTimeStore @Inject constructor(private val database: MarginDatabase) : DropTimeStore {
    private val metaDao get() = database.metaDao()

    override fun observeLearned(): Flow<LocalTime?> = metaDao.observe(MetaKey.DROP_TIME_LEARNED).map(::slotOf)

    override suspend fun saveLearned(slot: LocalTime) {
        metaDao.put(listOf(MetaEntity(MetaKey.DROP_TIME_LEARNED, slot.toString())))
    }

    private fun slotOf(stored: String?): LocalTime? =
        stored?.let { text -> runCatching { LocalTime.parse(text) }.getOrNull() }
}
