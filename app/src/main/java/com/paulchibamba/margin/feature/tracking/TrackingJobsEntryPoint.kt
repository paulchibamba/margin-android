package com.paulchibamba.margin.feature.tracking

import com.paulchibamba.margin.domain.usecase.PruneEventLog
import com.paulchibamba.margin.domain.usecase.RollUpEvents
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface TrackingJobsEntryPoint {
    fun rollUpEvents(): RollUpEvents
    fun pruneEventLog(): PruneEventLog
}
