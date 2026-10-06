package com.paulchibamba.margin.data.drop

import com.paulchibamba.margin.data.database.MarginDatabase
import com.paulchibamba.margin.domain.drop.DailyDrop
import com.paulchibamba.margin.domain.repository.DailyDropRepository
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomDailyDropRepository @Inject constructor(private val database: MarginDatabase) : DailyDropRepository {
    private val dao get() = database.dailyDropDao()

    override suspend fun forDate(date: LocalDate): DailyDrop? = dao.forDate(date.toString())?.let(DailyDropMapper::toDomain)

    override fun observe(date: LocalDate): Flow<DailyDrop?> =
        dao.observe(date.toString()).map { entity -> entity?.let(DailyDropMapper::toDomain) }

    override suspend fun save(drop: DailyDrop) = dao.save(DailyDropMapper.toEntity(drop))
}
