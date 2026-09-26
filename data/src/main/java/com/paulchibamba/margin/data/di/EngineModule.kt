package com.paulchibamba.margin.data.di

import android.content.Context
import com.paulchibamba.margin.data.database.MarginDatabase
import com.paulchibamba.margin.data.pack.AndroidAssetSource
import com.paulchibamba.margin.data.pack.PackImporter
import com.paulchibamba.margin.data.pack.PackMapper
import com.paulchibamba.margin.data.pack.PackReader
import com.paulchibamba.margin.data.startup.LogcatImportLogger
import com.paulchibamba.margin.data.startup.SystemClock
import com.paulchibamba.margin.domain.memory.CardSeededFuzz
import com.paulchibamba.margin.domain.memory.FuzzStrategy
import com.paulchibamba.margin.domain.repository.Clock
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlin.random.Random

@Module
@InstallIn(SingletonComponent::class)
object EngineModule {

    @Provides
    @Singleton
    fun clock(): Clock = SystemClock()

    @Provides
    @Singleton
    fun random(): Random = Random.Default

    @Provides
    fun fuzz(): FuzzStrategy = CardSeededFuzz

    @Provides
    @Singleton
    fun packImporter(@ApplicationContext context: Context, database: MarginDatabase): PackImporter =
        PackImporter(PackReader(AndroidAssetSource(context.assets)), PackMapper(), database, LogcatImportLogger())
}
