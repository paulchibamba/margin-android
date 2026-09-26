package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.actions.PostActionHandler
import com.paulchibamba.margin.domain.feed.FeedConfig
import com.paulchibamba.margin.domain.feed.FeedEngine
import com.paulchibamba.margin.domain.feed.PostExitHandler
import com.paulchibamba.margin.domain.memory.FsrsParameters
import com.paulchibamba.margin.domain.memory.FsrsScheduler
import com.paulchibamba.margin.domain.memory.FuzzStrategy
import com.paulchibamba.margin.domain.signals.EngagementCalculator
import com.paulchibamba.margin.domain.signals.GradeMapper
import javax.inject.Inject
import kotlin.random.Random

class LearningEngines @Inject constructor(private val random: Random, private val fuzz: FuzzStrategy) {

    fun feedEngine(retention: Double): FeedEngine =
        FeedEngine(FeedConfig(desiredRetention = retention), scheduler(retention), random)

    fun actionHandler(retention: Double): PostActionHandler = PostActionHandler(scheduler(retention))

    fun exitHandler(retention: Double): PostExitHandler =
        PostExitHandler(EngagementCalculator(), GradeMapper(), scheduler(retention))

    private fun scheduler(retention: Double): FsrsScheduler =
        FsrsScheduler(FsrsParameters.Default.copy(requestRetention = retention), fuzz)
}
