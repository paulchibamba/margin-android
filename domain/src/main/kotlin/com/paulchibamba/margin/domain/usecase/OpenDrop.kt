package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.drop.DailyDrop
import com.paulchibamba.margin.domain.drop.DropPage
import com.paulchibamba.margin.domain.drop.DropSession
import com.paulchibamba.margin.domain.drop.DropStage
import com.paulchibamba.margin.domain.feed.Candidate
import com.paulchibamba.margin.domain.feed.FeedState
import com.paulchibamba.margin.domain.repository.Clock
import com.paulchibamba.margin.domain.repository.SettingsRepository
import com.paulchibamba.margin.domain.tracking.Event
import com.paulchibamba.margin.domain.tracking.EventRecorder
import javax.inject.Inject

class OpenDrop @Inject constructor(
    private val prepareTodaysDrop: PrepareTodaysDrop,
    private val stateSource: FeedStateSource,
    private val dropPosts: DropPosts,
    private val settings: SettingsRepository,
    private val engines: LearningEngines,
    private val recorder: EventRecorder,
    private val clock: Clock,
) {
    suspend operator fun invoke(): DropSession? {
        val drop = prepareTodaysDrop() ?: return null
        val state = stateSource.current()
        val pages = pagesOf(drop, state)
        recorder.record(Event.DropEvent(DropStage.OPENED, drop.doneCount(state), drop.size))
        return DropSession(drop.size, pages)
    }

    private suspend fun pagesOf(drop: DailyDrop, state: FeedState): List<DropPage> {
        val posts = dropPosts.byId()
        val engine = engines.feedEngine(settings.desiredRetention())
        return drop.remainingIndices(state).mapNotNull { index ->
            val item = drop.items[index]
            val post = posts[item.postId] ?: return@mapNotNull null
            DropPage(index, engine.preview(state, Candidate(post, item.source), clock.now()))
        }
    }
}
