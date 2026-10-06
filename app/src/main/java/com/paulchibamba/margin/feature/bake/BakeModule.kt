package com.paulchibamba.margin.feature.bake

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface BakeModule {

    @Binds
    fun bakeScheduler(scheduler: WorkManagerBakeScheduler): BakeScheduler
}
