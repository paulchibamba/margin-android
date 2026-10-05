package com.paulchibamba.margin.data.rollup

import com.paulchibamba.margin.data.database.MarginDatabase
import com.paulchibamba.margin.data.database.entity.DailyRollupEntity
import com.paulchibamba.margin.domain.repository.RollupStore
import com.paulchibamba.margin.domain.rollup.DailyRollup
import java.time.Instant
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

class RoomRollupStore @Inject constructor(private val database: MarginDatabase) : RollupStore {
    private val rollupDao get() = database.rollupDao()

    override suspend fun save(rollup: DailyRollup) = rollupDao.upsert(entityOf(rollup))

    override suspend fun computedTimes(): Map<LocalDate, Instant> = rollupDao.stamps().associate { stamp ->
        LocalDate.parse(stamp.date) to Instant.ofEpochMilli(stamp.computedAt)
    }

    override fun observeFrom(date: LocalDate): Flow<List<DailyRollup>> =
        rollupDao.observeFrom(date.toString()).map { entities -> entities.mapNotNull(::rollupOf) }

    private fun entityOf(rollup: DailyRollup) = DailyRollupEntity(
        date = rollup.date.toString(),
        metrics = json.encodeToString(RollupJson.serializer(), RollupMapper.toJson(rollup.metrics)),
        computedAt = rollup.computedAt.toEpochMilli(),
        schemaVersion = SCHEMA_VERSION,
    )

    private fun rollupOf(entity: DailyRollupEntity): DailyRollup? = runCatching {
        val metrics = RollupMapper.fromJson(json.decodeFromString(RollupJson.serializer(), entity.metrics))
        DailyRollup(LocalDate.parse(entity.date), metrics, Instant.ofEpochMilli(entity.computedAt))
    }.getOrNull()

    companion object {
        const val SCHEMA_VERSION = 1
        private val json = Json {
            encodeDefaults = true
            ignoreUnknownKeys = true
        }
    }
}
