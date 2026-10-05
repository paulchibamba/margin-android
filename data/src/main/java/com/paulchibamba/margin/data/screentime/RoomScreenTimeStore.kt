package com.paulchibamba.margin.data.screentime

import androidx.room.withTransaction
import com.paulchibamba.margin.data.database.MarginDatabase
import com.paulchibamba.margin.data.database.MetaKey
import com.paulchibamba.margin.data.database.entity.AppCategoryOverrideEntity
import com.paulchibamba.margin.data.database.entity.MetaEntity
import com.paulchibamba.margin.data.database.entity.ScreenTimeDailyEntity
import com.paulchibamba.margin.domain.repository.ScreenTimeStore
import com.paulchibamba.margin.domain.screentime.AppCategory
import com.paulchibamba.margin.domain.screentime.AppScreenTime
import com.paulchibamba.margin.domain.screentime.PackageName
import java.time.LocalDate
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomScreenTimeStore @Inject constructor(private val database: MarginDatabase) : ScreenTimeStore {
    private val screenTimeDao get() = database.screenTimeDao()
    private val metaDao get() = database.metaDao()

    override suspend fun saveDay(date: LocalDate, apps: List<AppScreenTime>) =
        screenTimeDao.replaceDay(date.toString(), apps.map { app -> entityOf(date, app) })

    override suspend fun appsOn(date: LocalDate): List<AppScreenTime> =
        screenTimeDao.appsOn(date.toString()).map(::appOf)

    override suspend fun dates(): List<LocalDate> = screenTimeDao.dates().map(LocalDate::parse)

    override suspend fun datesWith(packageName: PackageName): List<LocalDate> =
        screenTimeDao.datesWith(packageName.value).map(LocalDate::parse)

    override suspend fun ingestedThrough(): LocalDate? = dateAt(MetaKey.SCREEN_TIME_INGESTED_THROUGH)

    override suspend fun markIngestedThrough(date: LocalDate) = putDate(MetaKey.SCREEN_TIME_INGESTED_THROUGH, date)

    override suspend fun ingestFrom(): LocalDate? = dateAt(MetaKey.SCREEN_TIME_INGEST_FROM)

    override suspend fun deleteAll(ingestFrom: LocalDate) = database.withTransaction {
        screenTimeDao.deleteAll()
        putDate(MetaKey.SCREEN_TIME_INGEST_FROM, ingestFrom)
    }

    override suspend fun overrides(): Map<PackageName, Boolean> =
        screenTimeDao.overrides().associate { override -> PackageName(override.packageName) to override.isDoom }

    override suspend fun setDoom(packageName: PackageName, isDoom: Boolean) =
        screenTimeDao.setDoom(AppCategoryOverrideEntity(packageName.value, isDoom))

    override fun observeTotalsFrom(date: LocalDate): Flow<List<AppScreenTime>> =
        screenTimeDao.observeTotalsFrom(date.toString()).map { entities -> entities.map(::appOf) }

    private suspend fun dateAt(key: String): LocalDate? = metaDao.get(key)?.let(LocalDate::parse)

    private suspend fun putDate(key: String, date: LocalDate) = metaDao.put(listOf(MetaEntity(key, date.toString())))

    private fun entityOf(date: LocalDate, app: AppScreenTime) = ScreenTimeDailyEntity(
        date = date.toString(),
        packageName = app.packageName.value,
        label = app.label,
        category = app.category.name.lowercase(),
        isDoom = app.isDoom,
        foregroundMs = app.foreground.inWholeMilliseconds,
    )

    private fun appOf(entity: ScreenTimeDailyEntity) = AppScreenTime(
        packageName = PackageName(entity.packageName),
        label = entity.label,
        category = categoryOf(entity.category),
        isDoom = entity.isDoom,
        foreground = entity.foregroundMs.milliseconds,
    )

    private fun categoryOf(name: String): AppCategory =
        AppCategory.entries.firstOrNull { it.name.equals(name, ignoreCase = true) } ?: AppCategory.OTHER
}
