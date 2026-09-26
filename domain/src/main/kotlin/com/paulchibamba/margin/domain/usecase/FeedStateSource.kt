package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.feed.FeedState
import com.paulchibamba.margin.domain.repository.ProgressRepository
import com.paulchibamba.margin.domain.repository.SettingsRepository
import javax.inject.Inject

class FeedStateSource @Inject constructor(
    private val progress: ProgressRepository,
    private val settings: SettingsRepository,
    private val engines: LearningEngines,
) {
    suspend fun current(): FeedState =
        progress.loadFeedState() ?: engines.feedEngine(settings.desiredRetention()).startingState()
}
