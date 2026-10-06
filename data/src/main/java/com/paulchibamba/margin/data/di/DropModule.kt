package com.paulchibamba.margin.data.di

import com.paulchibamba.margin.data.drop.RoomDailyDropRepository
import com.paulchibamba.margin.data.drop.RoomDropTimeStore
import com.paulchibamba.margin.domain.repository.DailyDropRepository
import com.paulchibamba.margin.domain.repository.DropTimeStore
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface DropModule {

    @Binds
    fun dropTimeStore(store: RoomDropTimeStore): DropTimeStore

    @Binds
    fun dailyDropRepository(repository: RoomDailyDropRepository): DailyDropRepository
}
