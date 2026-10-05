package com.paulchibamba.margin.data.repository

import com.paulchibamba.margin.data.database.MarginDatabase
import com.paulchibamba.margin.data.database.MetaKey
import com.paulchibamba.margin.data.database.entity.MetaEntity
import com.paulchibamba.margin.data.database.entity.PostSeenEntity
import com.paulchibamba.margin.data.database.entity.SavedPostEntity
import com.paulchibamba.margin.data.repository.mapper.ConceptProgressMapper
import com.paulchibamba.margin.data.repository.mapper.FeedHistoryMapper
import com.paulchibamba.margin.data.repository.mapper.AffinityNames
import com.paulchibamba.margin.data.repository.mapper.SourceNames
import com.paulchibamba.margin.domain.feed.FeedHistoryEntry
import com.paulchibamba.margin.domain.feed.FeedState
import com.paulchibamba.margin.domain.model.BookSlug
import com.paulchibamba.margin.domain.model.PostId
import com.paulchibamba.margin.domain.signals.FormatAffinity
import java.time.Instant

internal class FeedStateStorage(private val database: MarginDatabase) {
    private val metaDao get() = database.metaDao()
    private val feedStateDao get() = database.feedStateDao()

    suspend fun load(): FeedState? {
        val step = metaDao.get(MetaKey.FEED_STEP)?.toInt() ?: return null
        return FeedState(
            rewardAtStep = rewardAtStep() ?: step,
            step = step,
            conceptProgress = database.conceptProgressDao().all().associate(ConceptProgressMapper::toDomain),
            seenPosts = feedStateDao.seenPosts().associate { PostId(it.postId) to it.lastSeenStep },
            history = feedStateDao.history().map(FeedHistoryMapper::toDomain),
            bookLastNewStep = bookLastNewSteps(),
            lastPreviewAtStep = metaDao.get(MetaKey.LAST_PREVIEW_AT)?.toInt(),
            affinity = affinity(),
            savedPosts = feedStateDao.savedPosts().map { PostId(it.postId) }.toSet(),
        )
    }

    suspend fun save(state: FeedState, now: Instant) {
        metaDao.put(metaEntries(state))
        metaDao.delete(MetaKey.LEGACY_DELIGHT_AT)
        database.conceptProgressDao().upsert(state.conceptProgress.map { (id, progress) ->
            ConceptProgressMapper.toEntity(id, progress)
        })
        feedStateDao.upsertAffinity(AffinityNames.entitiesOf(state.affinity))
        saveNewlyShown(state.history, now)
        saveHistory(state.history)
        state.savedPosts.forEach { post -> feedStateDao.insertSaved(SavedPostEntity(post.value, now.toEpochMilli())) }
    }

    private suspend fun rewardAtStep(): Int? =
        (metaDao.get(MetaKey.REWARD_AT) ?: metaDao.get(MetaKey.LEGACY_DELIGHT_AT))?.toInt()

    private suspend fun affinity(): FormatAffinity = AffinityNames.affinityOf(feedStateDao.affinity())

    private fun metaEntries(state: FeedState): List<MetaEntity> = buildList {
        add(MetaEntity(MetaKey.FEED_STEP, state.step.toString()))
        add(MetaEntity(MetaKey.REWARD_AT, state.rewardAtStep.toString()))
        state.lastPreviewAtStep?.let { step -> add(MetaEntity(MetaKey.LAST_PREVIEW_AT, step.toString())) }
        state.bookLastNewStep.forEach { (book, step) ->
            add(MetaEntity(MetaKey.bookLastNew(book.value), step.toString()))
        }
    }

    private suspend fun bookLastNewSteps(): Map<BookSlug, Int> =
        metaDao.withPrefix(MetaKey.bookLastNew("")).associate { entry ->
            BookSlug(entry.key.removePrefix(MetaKey.bookLastNew(""))) to entry.value.toInt()
        }

    private suspend fun saveNewlyShown(history: List<FeedHistoryEntry>, now: Instant) {
        val latestSaved = feedStateDao.latestSeenStep() ?: 0
        val newlyShown = history.filter { entry -> entry.step > latestSaved }
        feedStateDao.upsertSeen(newlyShown.map { entry -> seenAfterShowing(entry, now) })
    }

    private suspend fun seenAfterShowing(entry: FeedHistoryEntry, now: Instant): PostSeenEntity {
        val at = now.toEpochMilli()
        val source = SourceNames.nameOf(entry.source)
        val previous = feedStateDao.seen(entry.postId.value)
            ?: return PostSeenEntity(entry.postId.value, at, at, entry.step, 1, null, null, null, source)
        return previous.copy(
            lastSeenAt = at,
            lastSeenStep = entry.step,
            times = previous.times + 1,
            lastSource = source,
        )
    }

    private suspend fun saveHistory(history: List<FeedHistoryEntry>) {
        feedStateDao.upsertHistory(history.takeLast(HISTORY_KEPT).map(FeedHistoryMapper::toEntity))
        feedStateDao.trimHistory(keep = HISTORY_KEPT)
    }

    private companion object {
        const val HISTORY_KEPT = 30
    }
}
