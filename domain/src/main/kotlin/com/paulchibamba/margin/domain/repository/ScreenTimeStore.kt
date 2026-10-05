package com.paulchibamba.margin.domain.repository

import com.paulchibamba.margin.domain.screentime.AppScreenTime
import com.paulchibamba.margin.domain.screentime.PackageName
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

interface ScreenTimeStore {
    suspend fun saveDay(date: LocalDate, apps: List<AppScreenTime>)
    suspend fun appsOn(date: LocalDate): List<AppScreenTime>
    suspend fun dates(): List<LocalDate>
    suspend fun datesWith(packageName: PackageName): List<LocalDate>
    suspend fun ingestedThrough(): LocalDate?
    suspend fun markIngestedThrough(date: LocalDate)
    suspend fun ingestFrom(): LocalDate?
    suspend fun deleteAll(ingestFrom: LocalDate)
    suspend fun overrides(): Map<PackageName, Boolean>
    suspend fun setDoom(packageName: PackageName, isDoom: Boolean)
    fun observeTotalsFrom(date: LocalDate): Flow<List<AppScreenTime>>
}
