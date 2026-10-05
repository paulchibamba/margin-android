package com.paulchibamba.margin.data.repository

import androidx.room.withTransaction
import com.paulchibamba.margin.data.database.MarginDatabase
import com.paulchibamba.margin.data.database.MetaKey
import com.paulchibamba.margin.data.database.entity.ChapterKnownEntity
import com.paulchibamba.margin.data.database.entity.MetaEntity
import com.paulchibamba.margin.data.database.entity.NoteReadEntity
import com.paulchibamba.margin.data.repository.mapper.LogMapper
import com.paulchibamba.margin.data.repository.mapper.SettingsMapper
import com.paulchibamba.margin.domain.actions.ActionLogEntry
import com.paulchibamba.margin.domain.feed.FeedState
import com.paulchibamba.margin.domain.feed.ReviewLogEntry
import com.paulchibamba.margin.domain.memory.Rating
import com.paulchibamba.margin.domain.model.BookSlug
import com.paulchibamba.margin.domain.model.ChapterRef
import com.paulchibamba.margin.domain.model.NoteId
import com.paulchibamba.margin.domain.model.PostId
import com.paulchibamba.margin.domain.progression.ReadingState
import com.paulchibamba.margin.domain.repository.ProgressRepository
import com.paulchibamba.margin.domain.rewards.ActivityChange
import com.paulchibamba.margin.domain.rewards.Badge
import com.paulchibamba.margin.domain.rewards.BadgeKind
import com.paulchibamba.margin.domain.rewards.DailyActivity
import com.paulchibamba.margin.domain.signals.AnswerOutcome
import com.paulchibamba.margin.domain.signals.PostExit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomProgressRepository @Inject constructor(private val database: MarginDatabase) : ProgressRepository {
    private val feedState = FeedStateStorage(database)

    override suspend fun loadFeedState(): FeedState? = database.withTransaction { feedState.load() }

    override suspend fun saveFeedState(state: FeedState, now: Instant) {
        database.withTransaction { feedState.save(state, now) }
    }

    override fun observeFeedState(): Flow<FeedState?> =
        database.invalidationTracker.createFlow(*FEED_STATE_TABLES).map { loadFeedState() }

    override suspend fun recordExit(post: PostId, exit: PostExit, engagement: Double) {
        database.feedStateDao().recordExit(post.value, exit.dwell.inWholeMilliseconds, engagement, isCorrect(exit))
    }

    override suspend fun firstSeenTimes(): Map<PostId, Instant> = database.feedStateDao().seenPosts()
        .associate { seen -> PostId(seen.postId) to Instant.ofEpochMilli(seen.firstSeenAt) }

    override suspend fun appendAction(entry: ActionLogEntry) {
        database.feedLogDao().insertAction(LogMapper.toEntity(entry))
    }

    override suspend fun appendReview(entry: ReviewLogEntry) {
        database.feedLogDao().insertReview(LogMapper.toEntity(entry))
    }

    override fun observeActions(): Flow<List<ActionLogEntry>> =
        database.feedLogDao().actions().map { entries -> entries.map(LogMapper::toDomain) }

    override fun observeReviews(): Flow<List<ReviewLogEntry>> =
        database.feedLogDao().reviews().map { entries -> entries.map(LogMapper::toDomain) }

    override suspend fun reading(): ReadingState = observeReading().first()

    override fun observeReading(): Flow<ReadingState> = combine(
        database.readingDao().notesRead(),
        database.readingDao().chaptersKnown(),
        database.metaDao().observe(MetaKey.LAST_NOTE),
    ) { notesRead, chaptersKnown, lastNote ->
        ReadingState(
            readNotes = notesRead.map { NoteId(it.noteId) }.toSet(),
            knownChapters = chaptersKnown.map { ChapterRef(BookSlug(it.bookSlug), it.chapter) }.toSet(),
            lastNote = lastNote?.let(::NoteId),
        )
    }

    override suspend fun markNoteRead(note: NoteId, at: Instant): Boolean = database.withTransaction {
        val rowId = database.readingDao().markNoteRead(NoteReadEntity(note.value, at.toEpochMilli()))
        database.metaDao().put(listOf(MetaEntity(MetaKey.LAST_NOTE, note.value)))
        rowId != NOT_INSERTED
    }

    override suspend fun setLastNote(note: NoteId) {
        database.metaDao().put(listOf(MetaEntity(MetaKey.LAST_NOTE, note.value)))
    }

    override suspend fun markChapterKnown(chapter: ChapterRef, at: Instant) {
        val known = ChapterKnownEntity(chapter.bookSlug.value, chapter.chapter, at.toEpochMilli())
        database.readingDao().markChapterKnown(known)
    }

    override suspend fun unmarkChapterKnown(chapter: ChapterRef) {
        database.readingDao().unmarkChapterKnown(chapter.bookSlug.value, chapter.chapter)
    }

    override fun observeActivity(): Flow<List<DailyActivity>> =
        database.activityDao().all().map { days -> days.map(SettingsMapper::toDomain) }

    override suspend fun addActivity(date: LocalDate, postsSeen: Int, notesRead: Int): ActivityChange =
        database.withTransaction {
            val before = database.activityDao().on(date.toString())?.let(SettingsMapper::toDomain)
            val after = (before ?: DailyActivity(date)).let { day ->
                day.copy(postsSeen = day.postsSeen + postsSeen, notesRead = day.notesRead + notesRead)
            }
            database.activityDao().upsert(SettingsMapper.toEntity(after))
            ActivityChange(before, after)
        }

    override suspend fun shownBadges(): Set<Badge> =
        database.metaDao().withPrefix(BADGE_PREFIX).mapNotNull { entry -> badgeOf(entry.key) }.toSet()

    override suspend fun markBadgesShown(badges: Collection<Badge>) {
        database.metaDao().put(badges.map { badge -> MetaEntity(badgeKey(badge), SHOWN) })
    }

    override suspend fun clearProgress() {
        database.progressResetDao().clearProgress()
    }

    private fun isCorrect(exit: PostExit): Boolean? = when (val answer = exit.answer) {
        null -> null
        AnswerOutcome.Correct -> true
        AnswerOutcome.Wrong -> false
        is AnswerOutcome.SelfGraded -> answer.rating != Rating.AGAIN
    }

    private fun badgeKey(badge: Badge) = MetaKey.badgeShown(badge.bookSlug.value, badge.kind.name.lowercase())

    private fun badgeOf(key: String): Badge? {
        val (book, kind) = key.removePrefix(BADGE_PREFIX).split(":").takeIf { it.size == 2 } ?: return null
        return Badge(BookSlug(book), BadgeKind.valueOf(kind.uppercase()))
    }

    private companion object {
        const val NOT_INSERTED = -1L
        const val SHOWN = "1"
        val BADGE_PREFIX = MetaKey.badgeShown("", "").removeSuffix(":")
        val FEED_STATE_TABLES = arrayOf("meta", "concept_progress", "post_seen", "feed_history", "format_affinity",
            "saved_post")
    }
}
