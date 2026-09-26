package com.paulchibamba.margin.data.di

import android.content.Context
import androidx.room.Room
import com.paulchibamba.margin.data.database.MarginDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun database(@ApplicationContext context: Context): MarginDatabase =
        Room.databaseBuilder(context, MarginDatabase::class.java, MarginDatabase.NAME).build()
}
