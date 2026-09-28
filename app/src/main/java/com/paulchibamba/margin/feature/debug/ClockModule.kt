package com.paulchibamba.margin.feature.debug

import android.content.Context
import com.paulchibamba.margin.data.startup.PreferencesClockOffsetStore
import com.paulchibamba.margin.data.startup.SystemClock
import com.paulchibamba.margin.domain.repository.Clock
import com.paulchibamba.margin.domain.time.OffsetClock
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ClockModule {

    @Provides
    @Singleton
    fun offsetClock(@ApplicationContext context: Context): OffsetClock =
        OffsetClock(SystemClock(), PreferencesClockOffsetStore(context))

    @Provides
    @Singleton
    fun clock(offsetClock: Provider<OffsetClock>): Clock =
        if (DebugTools.isEnabled) offsetClock.get() else SystemClock()
}
