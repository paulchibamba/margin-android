package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.memory.Rating
import com.paulchibamba.margin.domain.model.Post
import com.paulchibamba.margin.domain.repository.Clock
import com.paulchibamba.margin.domain.repository.SettingsRepository
import javax.inject.Inject
import kotlin.time.Duration

class PreviewIntervals @Inject constructor(
    private val stateSource: FeedStateSource,
    private val settings: SettingsRepository,
    private val engines: LearningEngines,
    private val clock: Clock,
) {
    suspend operator fun invoke(post: Post): Map<Rating, Duration> {
        val card = stateSource.current().progressOf(post.conceptId).card ?: return emptyMap()
        return engines.scheduler(settings.desiredRetention()).previewIntervals(card, clock.now())
    }
}
