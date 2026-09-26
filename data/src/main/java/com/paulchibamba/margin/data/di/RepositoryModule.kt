package com.paulchibamba.margin.data.di

import com.paulchibamba.margin.data.repository.RoomContentRepository
import com.paulchibamba.margin.data.repository.RoomProgressRepository
import com.paulchibamba.margin.data.repository.RoomSettingsRepository
import com.paulchibamba.margin.domain.repository.ContentRepository
import com.paulchibamba.margin.domain.repository.ProgressRepository
import com.paulchibamba.margin.domain.repository.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Binds
    fun contentRepository(repository: RoomContentRepository): ContentRepository

    @Binds
    fun progressRepository(repository: RoomProgressRepository): ProgressRepository

    @Binds
    fun settingsRepository(repository: RoomSettingsRepository): SettingsRepository
}
