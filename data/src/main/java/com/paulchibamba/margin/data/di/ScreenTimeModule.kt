package com.paulchibamba.margin.data.di

import com.paulchibamba.margin.data.screentime.AndroidUsageSource
import com.paulchibamba.margin.data.screentime.RoomScreenTimeStore
import com.paulchibamba.margin.domain.repository.ScreenTimeStore
import com.paulchibamba.margin.domain.repository.UsageSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object ScreenTimeModule {

    @Provides
    fun screenTimeStore(store: RoomScreenTimeStore): ScreenTimeStore = store

    @Provides
    fun usageSource(source: AndroidUsageSource): UsageSource = source
}
