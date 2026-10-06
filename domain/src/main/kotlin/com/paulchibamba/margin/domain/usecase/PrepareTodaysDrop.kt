package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.drop.DailyDrop
import com.paulchibamba.margin.domain.drop.DropClosings
import com.paulchibamba.margin.domain.drop.DropHeadline
import com.paulchibamba.margin.domain.drop.DropRequest
import com.paulchibamba.margin.domain.drop.DropSlot
import com.paulchibamba.margin.domain.drop.DropStage
import com.paulchibamba.margin.domain.feed.LearningLibrary
import com.paulchibamba.margin.domain.model.Post
import com.paulchibamba.margin.domain.progress.GeneratedPost
import com.paulchibamba.margin.domain.progress.RewardHistory
import com.paulchibamba.margin.domain.repository.BakeStateStore
import com.paulchibamba.margin.domain.repository.Clock
import com.paulchibamba.margin.domain.repository.DailyDropRepository
import com.paulchibamba.margin.domain.repository.GeneratedPostRepository
import com.paulchibamba.margin.domain.repository.SettingsRepository
import com.paulchibamba.margin.domain.time.today
import com.paulchibamba.margin.domain.tracking.Event
import com.paulchibamba.margin.domain.tracking.EventRecorder
import java.time.LocalDate
import javax.inject.Inject
import kotlin.random.Random

class PrepareTodaysDrop @Inject constructor(
    private val drops: DailyDropRepository,
    private val libraryLoader: LibraryLoader,
    private val stateSource: FeedStateSource,
    private val settings: SettingsRepository,
    private val engines: LearningEngines,
    private val buildFacts: BuildProgressFacts,
    private val generatedPosts: GeneratedPostRepository,
    private val bakeState: BakeStateStore,
    private val recorder: EventRecorder,
    private val clock: Clock,
    private val lock: FeedStateLock,
    private val random: Random,
) {
    suspend operator fun invoke(): DailyDrop? = lock.withLock {
        val today = clock.today()
        drops.forDate(today) ?: composeFor(today)?.also { drop -> announce(drop) }
    }

    private suspend fun composeFor(today: LocalDate): DailyDrop? {
        val library = libraryLoader.load()
        val fallbacks = fallbackClosings()
        val headline = bakeState.load().nextDropHeadline
        val state = stateSource.current()
        val request = DropRequest(library, state, clock.now(), postsOf(fallbacks, library), headline?.postId)
        val drop = engines.dropComposer(settings.desiredRetention()).compose(today, request)
        if (drop.items.isEmpty()) return null
        generatedPosts.insert(fallbacks.filter { fallback -> fallback.id == drop.closingPostId() })
        return drop.copy(headline = headline?.takeIf { it.postId == drop.closingPostId() }?.let(DropHeadline::text))
    }

    private suspend fun announce(drop: DailyDrop) {
        drops.save(drop)
        bakeState.save(bakeState.load().copy(nextDropHeadline = null))
        recorder.record(Event.DropEvent(DropStage.SHOWN, position = 0, size = drop.size))
    }

    private suspend fun fallbackClosings(): List<GeneratedPost> {
        val history = RewardHistory(generatedPosts.all().map(GeneratedPost::toPastReward))
        return DropClosings(random).templatesFor(buildFacts(history), history, clock.now())
    }

    private fun postsOf(generated: List<GeneratedPost>, library: LearningLibrary): List<Post> {
        val books = library.conceptsInBookOrder.associate { concept -> concept.id to concept.bookSlug }
        return generated.mapNotNull { post -> post.conceptIds.firstOrNull()?.let(books::get)?.let(post::toPost) }
    }

    private fun DailyDrop.closingPostId() = items.lastOrNull { item -> item.slot == DropSlot.CLOSING }?.postId
}
