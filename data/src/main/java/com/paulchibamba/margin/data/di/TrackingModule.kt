package com.paulchibamba.margin.data.di

import android.util.Log
import com.paulchibamba.margin.data.database.MarginDatabase
import com.paulchibamba.margin.data.tracking.RoomEventLog
import com.paulchibamba.margin.data.tracking.RoomEventSink
import com.paulchibamba.margin.data.tracking.RoomSessionTally
import com.paulchibamba.margin.domain.repository.EventLog
import com.paulchibamba.margin.domain.repository.EventSink
import com.paulchibamba.margin.domain.repository.SessionTally
import com.paulchibamba.margin.domain.tracking.EventRecorder
import com.paulchibamba.margin.domain.tracking.SessionId
import com.paulchibamba.margin.domain.tracking.SessionIdFactory
import com.paulchibamba.margin.domain.tracking.SessionTracker
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.util.UUID
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

@Module
@InstallIn(SingletonComponent::class)
object TrackingModule {
    private const val TAG = "MarginEvents"

    @Provides
    @Singleton
    fun eventSink(database: MarginDatabase): EventSink = RoomEventSink(database, flushScope())

    @Provides
    fun eventLog(log: RoomEventLog): EventLog = log

    @Provides
    fun sessionTally(tally: RoomSessionTally): SessionTally = tally

    @Provides
    fun sessionIds(): SessionIdFactory = SessionIdFactory { SessionId(UUID.randomUUID().toString()) }

    @Provides
    fun eventRecorder(tracker: SessionTracker): EventRecorder = tracker

    private fun flushScope(): CoroutineScope {
        val logFailure = CoroutineExceptionHandler { _, error -> Log.w(TAG, "Couldn't write events", error) }
        return CoroutineScope(SupervisorJob() + Dispatchers.IO + logFailure)
    }
}
