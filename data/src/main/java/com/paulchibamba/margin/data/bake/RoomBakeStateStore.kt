package com.paulchibamba.margin.data.bake

import com.paulchibamba.margin.data.database.MarginDatabase
import com.paulchibamba.margin.data.database.MetaKey
import com.paulchibamba.margin.data.database.entity.MetaEntity
import com.paulchibamba.margin.domain.bake.BakeState
import com.paulchibamba.margin.domain.drop.DropHeadline
import com.paulchibamba.margin.domain.model.PostId
import com.paulchibamba.margin.domain.repository.BakeStateStore
import java.time.Instant
import javax.inject.Inject

class RoomBakeStateStore @Inject constructor(private val database: MarginDatabase) : BakeStateStore {
    private val metaDao get() = database.metaDao()

    override suspend fun load(): BakeState = BakeState(
        lastBakeAt = metaDao.get(MetaKey.LAST_BAKE_AT)?.toLongOrNull()?.let(Instant::ofEpochMilli),
        seenSeeds = seenSeedsOf(metaDao.get(MetaKey.BAKE_SEEN_SEEDS)),
        nextDropHeadline = headlineOf(metaDao.get(NEXT_DROP_HEADLINE), metaDao.get(NEXT_DROP_HEADLINE_POST)),
    )

    override suspend fun save(state: BakeState) {
        val entries = listOfNotNull(
            state.lastBakeAt?.let { at -> MetaEntity(MetaKey.LAST_BAKE_AT, at.toEpochMilli().toString()) },
            MetaEntity(MetaKey.BAKE_SEEN_SEEDS, state.seenSeeds.sorted().joinToString(SEPARATOR)),
        ) + headlineEntries(state.nextDropHeadline)
        metaDao.put(entries)
        if (state.nextDropHeadline == null) HEADLINE_KEYS.forEach { key -> metaDao.delete(key) }
    }

    private fun headlineEntries(headline: DropHeadline?): List<MetaEntity> = headline?.let {
        listOf(MetaEntity(NEXT_DROP_HEADLINE, it.text), MetaEntity(NEXT_DROP_HEADLINE_POST, it.postId.value))
    }.orEmpty()

    private fun headlineOf(text: String?, post: String?): DropHeadline? =
        if (text == null || post == null) null else DropHeadline(text, PostId(post))

    private fun seenSeedsOf(stored: String?): Set<String> =
        stored?.split(SEPARATOR)?.filter(String::isNotEmpty).orEmpty().toSet()

    private companion object {
        const val SEPARATOR = ","
        const val NEXT_DROP_HEADLINE = MetaKey.NEXT_DROP_HEADLINE
        const val NEXT_DROP_HEADLINE_POST = MetaKey.NEXT_DROP_HEADLINE_POST
        val HEADLINE_KEYS = listOf(NEXT_DROP_HEADLINE, NEXT_DROP_HEADLINE_POST)
    }
}
