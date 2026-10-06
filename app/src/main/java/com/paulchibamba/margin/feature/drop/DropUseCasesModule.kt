package com.paulchibamba.margin.feature.drop

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface DropUseCasesModule {

    @Binds
    fun dropUseCases(useCases: DomainDropUseCases): DropUseCases
}
