package com.paulchibamba.margin.data.progress

import com.paulchibamba.margin.data.database.MarginDatabase
import com.paulchibamba.margin.data.repository.mapper.LogMapper
import com.paulchibamba.margin.domain.model.ConceptId
import com.paulchibamba.margin.domain.model.NoteId
import com.paulchibamba.margin.domain.model.PostId
import com.paulchibamba.margin.domain.progress.GeneratedPost
import com.paulchibamba.margin.domain.progress.RewardExpiry
import com.paulchibamba.margin.domain.repository.GeneratedPostRepository
import kotlinx.coroutines.flow.first
import java.time.Instant
import javax.inject.Inject

class RoomGeneratedPostRepository @Inject constructor(private val database: MarginDatabase) : GeneratedPostRepository {
    private val dao get() = database.generatedPostDao()

    override suspend fun insert(posts: List<GeneratedPost>): Int =
        dao.insert(posts.map(GeneratedPostMapper::toEntity)).count { rowId -> rowId != NOT_INSERTED }

    override suspend fun fresh(now: Instant): List<GeneratedPost> {
        val expiry = expiry()
        return dao.unseen().mapNotNull(GeneratedPostMapper::toDomain).filter { post -> expiry.isFresh(post, now) }
    }

    override suspend fun all(): List<GeneratedPost> = dao.all().mapNotNull(GeneratedPostMapper::toDomain)

    override suspend fun markShown(post: PostId, at: Instant) = dao.markShown(post.value, at.toEpochMilli())

    override suspend fun markLess(post: PostId) = dao.markLess(post.value)

    private suspend fun expiry() = RewardExpiry(
        readNotes = dao.readNoteIds().map(::NoteId).toSet(),
        reviews = database.feedLogDao().reviews().first().map(LogMapper::toDomain),
        existingConcepts = dao.conceptIds().map(::ConceptId).toSet(),
    )

    private companion object {
        const val NOT_INSERTED = -1L
    }
}
