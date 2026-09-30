package com.paulchibamba.margin.domain.repository

import com.paulchibamba.margin.domain.actions.ActionLogEntry
import com.paulchibamba.margin.domain.feed.FeedState
import com.paulchibamba.margin.domain.feed.ReviewLogEntry
import com.paulchibamba.margin.domain.model.ChapterRef
import com.paulchibamba.margin.domain.model.NoteId
import com.paulchibamba.margin.domain.model.PostId
import com.paulchibamba.margin.domain.progression.ReadingState
import com.paulchibamba.margin.domain.rewards.ActivityChange
import com.paulchibamba.margin.domain.rewards.Badge
import com.paulchibamba.margin.domain.rewards.DailyActivity
import com.paulchibamba.margin.domain.signals.PostExit
import kotlinx.coroutines.flow.Flow
import java.time.Instant
import java.time.LocalDate

interface ProgressRepository {
    suspend fun loadFeedState(): FeedState?
    suspend fun saveFeedState(state: FeedState, now: Instant)
    fun observeFeedState(): Flow<FeedState?>
    suspend fun recordExit(post: PostId, exit: PostExit, engagement: Double)

    suspend fun appendAction(entry: ActionLogEntry)
    suspend fun appendReview(entry: ReviewLogEntry)
    fun observeActions(): Flow<List<ActionLogEntry>>
    fun observeReviews(): Flow<List<ReviewLogEntry>>

    suspend fun reading(): ReadingState
    fun observeReading(): Flow<ReadingState>
    suspend fun markNoteRead(note: NoteId, at: Instant): Boolean
    suspend fun setLastNote(note: NoteId)
    suspend fun markChapterKnown(chapter: ChapterRef, at: Instant)
    suspend fun unmarkChapterKnown(chapter: ChapterRef)

    fun observeActivity(): Flow<List<DailyActivity>>
    suspend fun addActivity(date: LocalDate, postsSeen: Int, notesRead: Int): ActivityChange

    suspend fun shownBadges(): Set<Badge>
    suspend fun markBadgesShown(badges: Collection<Badge>)

    suspend fun clearProgress()
}
