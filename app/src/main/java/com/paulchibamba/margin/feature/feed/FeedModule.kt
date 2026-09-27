package com.paulchibamba.margin.feature.feed

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface FeedModule {

    @Binds
    fun feedUseCases(useCases: DomainFeedUseCases): FeedUseCases
}
