package com.paulchibamba.margin.data.di

import android.content.Context
import com.paulchibamba.margin.data.cover.CoverDirectory
import com.paulchibamba.margin.data.cover.FileCoverImageStore
import com.paulchibamba.margin.domain.repository.CoverImageStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoverModule {

    @Provides
    @Singleton
    fun coverDirectory(@ApplicationContext context: Context): CoverDirectory =
        CoverDirectory(File(context.filesDir, CoverDirectory.NAME))

    @Provides
    fun coverImageStore(@ApplicationContext context: Context, directory: CoverDirectory): CoverImageStore =
        FileCoverImageStore(context.contentResolver, directory)
}
