package com.paulchibamba.margin.data.di

import com.paulchibamba.margin.data.bake.AndroidConnectivity
import com.paulchibamba.margin.data.bake.RoomBakeStateStore
import com.paulchibamba.margin.domain.repository.BakeStateStore
import com.paulchibamba.margin.domain.repository.Connectivity
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface BakeModule {

    @Binds
    fun bakeStateStore(store: RoomBakeStateStore): BakeStateStore

    @Binds
    fun connectivity(connectivity: AndroidConnectivity): Connectivity
}
