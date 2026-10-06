package com.paulchibamba.margin.data.bake

import com.paulchibamba.margin.data.database.MarginDatabase
import com.paulchibamba.margin.data.database.MetaKey
import com.paulchibamba.margin.data.database.entity.MetaEntity
import com.paulchibamba.margin.domain.bake.BakeState
import com.paulchibamba.margin.domain.repository.BakeStateStore
import java.time.Instant
import javax.inject.Inject

class RoomBakeStateStore @Inject constructor(private val database: MarginDatabase) : BakeStateStore {
    private val metaDao get() = database.metaDao()

    override suspend fun load(): BakeState = BakeState(
        lastBakeAt = metaDao.get(MetaKey.LAST_BAKE_AT)?.toLongOrNull()?.let(Instant::ofEpochMilli),
        seenSeeds = seenSeedsOf(metaDao.get(MetaKey.BAKE_SEEN_SEEDS)),
        nextDropHeadline = metaDao.get(MetaKey.NEXT_DROP_HEADLINE),
    )

    override suspend fun save(state: BakeState) {
        val entries = listOfNotNull(
            state.lastBakeAt?.let { at -> MetaEntity(MetaKey.LAST_BAKE_AT, at.toEpochMilli().toString()) },
            MetaEntity(MetaKey.BAKE_SEEN_SEEDS, state.seenSeeds.sorted().joinToString(SEPARATOR)),
            state.nextDropHeadline?.let { headline -> MetaEntity(MetaKey.NEXT_DROP_HEADLINE, headline) },
        )
        metaDao.put(entries)
        if (state.nextDropHeadline == null) metaDao.delete(MetaKey.NEXT_DROP_HEADLINE)
    }

    private fun seenSeedsOf(stored: String?): Set<String> =
        stored?.split(SEPARATOR)?.filter(String::isNotEmpty).orEmpty().toSet()

    private companion object {
        const val SEPARATOR = ","
    }
}
