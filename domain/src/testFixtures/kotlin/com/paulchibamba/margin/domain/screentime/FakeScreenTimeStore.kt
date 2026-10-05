package com.paulchibamba.margin.domain.screentime

import com.paulchibamba.margin.domain.repository.ScreenTimeStore
import java.time.LocalDate
import kotlin.time.Duration
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeScreenTimeStore : ScreenTimeStore {
    private val days = MutableStateFlow<Map<LocalDate, List<AppScreenTime>>>(emptyMap())
    val overrides = mutableMapOf<PackageName, Boolean>()
    var ingestedThrough: LocalDate? = null
    var ingestFrom: LocalDate? = null

    val saved: Map<LocalDate, List<AppScreenTime>> get() = days.value

    override suspend fun saveDay(date: LocalDate, apps: List<AppScreenTime>) {
        days.value += date to apps
    }

    override suspend fun appsOn(date: LocalDate): List<AppScreenTime> = saved[date].orEmpty()

    override suspend fun dates(): List<LocalDate> = saved.keys.sorted()

    override suspend fun datesWith(packageName: PackageName): List<LocalDate> =
        saved.filterValues { apps -> apps.any { it.packageName == packageName } }.keys.sorted()

    override suspend fun ingestedThrough(): LocalDate? = ingestedThrough

    override suspend fun markIngestedThrough(date: LocalDate) {
        ingestedThrough = date
    }

    override suspend fun ingestFrom(): LocalDate? = ingestFrom

    override suspend fun deleteAll(ingestFrom: LocalDate) {
        days.value = emptyMap()
        this.ingestFrom = ingestFrom
    }

    override suspend fun overrides(): Map<PackageName, Boolean> = overrides.toMap()

    override suspend fun setDoom(packageName: PackageName, isDoom: Boolean) {
        overrides[packageName] = isDoom
        days.value = saved.mapValues { (_, apps) ->
            apps.map { app -> if (app.packageName == packageName) app.copy(isDoom = isDoom) else app }
        }
    }

    override fun observeTotalsFrom(date: LocalDate): Flow<List<AppScreenTime>> = days.map { all ->
        all.filterKeys { !it.isBefore(date) }.values.flatten()
            .groupBy(AppScreenTime::packageName)
            .map { (_, apps) -> apps.last().copy(foreground = totalOf(apps)) }
            .sortedByDescending(AppScreenTime::foreground)
    }

    private fun totalOf(apps: List<AppScreenTime>): Duration =
        apps.fold(Duration.ZERO) { total, app -> total + app.foreground }
}
