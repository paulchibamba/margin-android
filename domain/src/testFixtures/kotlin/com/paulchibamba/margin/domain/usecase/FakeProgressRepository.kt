package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.actions.ActionLogEntry
import com.paulchibamba.margin.domain.feed.FeedState
import com.paulchibamba.margin.domain.feed.ReviewLogEntry
import com.paulchibamba.margin.domain.model.ChapterRef
import com.paulchibamba.margin.domain.model.NoteId
import com.paulchibamba.margin.domain.model.PostId
import com.paulchibamba.margin.domain.progression.ReadingState
import com.paulchibamba.margin.domain.repository.ProgressRepository
import com.paulchibamba.margin.domain.rewards.ActivityChange
import com.paulchibamba.margin.domain.rewards.Badge
import com.paulchibamba.margin.domain.rewards.DailyActivity
import com.paulchibamba.margin.domain.signals.PostExit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.Instant
import java.time.LocalDate

class FakeProgressRepository : ProgressRepository {
    val feedState = MutableStateFlow<FeedState?>(null)
    val actions = MutableStateFlow<List<ActionLogEntry>>(emptyList())
    val reviews = MutableStateFlow<List<ReviewLogEntry>>(emptyList())
    val reading = MutableStateFlow(ReadingState())
    val activity = MutableStateFlow<List<DailyActivity>>(emptyList())
    val exits = mutableListOf<Pair<PostId, Double>>()
    val badges = mutableSetOf<Badge>()
    val firstSeen = mutableMapOf<PostId, Instant>()

    override suspend fun loadFeedState() = feedState.value
    override suspend fun saveFeedState(state: FeedState, now: Instant) { feedState.value = state }
    override fun observeFeedState(): Flow<FeedState?> = feedState
    override suspend fun recordExit(post: PostId, exit: PostExit, engagement: Double) { exits += post to engagement }
    override suspend fun firstSeenTimes(): Map<PostId, Instant> = firstSeen.toMap()

    override suspend fun appendAction(entry: ActionLogEntry) { actions.value += entry }
    override suspend fun appendReview(entry: ReviewLogEntry) { reviews.value += entry }
    override fun observeActions(): Flow<List<ActionLogEntry>> = actions
    override fun observeReviews(): Flow<List<ReviewLogEntry>> = reviews

    override suspend fun reading() = reading.value
    override fun observeReading(): Flow<ReadingState> = reading

    override suspend fun markNoteRead(note: NoteId, at: Instant): Boolean {
        val isNew = note !in reading.value.readNotes
        reading.value = reading.value.copy(readNotes = reading.value.readNotes + note, lastNote = note)
        return isNew
    }

    override suspend fun setLastNote(note: NoteId) {
        reading.value = reading.value.copy(lastNote = note)
    }

    override suspend fun markChapterKnown(chapter: ChapterRef, at: Instant) {
        reading.value = reading.value.copy(knownChapters = reading.value.knownChapters + chapter)
    }

    override suspend fun unmarkChapterKnown(chapter: ChapterRef) {
        reading.value = reading.value.copy(knownChapters = reading.value.knownChapters - chapter)
    }

    override fun observeActivity(): Flow<List<DailyActivity>> = activity

    override suspend fun addActivity(date: LocalDate, postsSeen: Int, notesRead: Int): ActivityChange {
        val before = activity.value.firstOrNull { it.date == date }
        val after = (before ?: DailyActivity(date)).let {
            it.copy(postsSeen = it.postsSeen + postsSeen, notesRead = it.notesRead + notesRead)
        }
        activity.value = activity.value.filterNot { it.date == date } + after
        return ActivityChange(before, after)
    }

    override suspend fun shownBadges(): Set<Badge> = badges.toSet()
    override suspend fun markBadgesShown(badges: Collection<Badge>) { this.badges += badges }

    override suspend fun clearProgress() {
        feedState.value = null
        actions.value = emptyList()
        reviews.value = emptyList()
        reading.value = ReadingState()
        activity.value = emptyList()
        exits.clear()
        badges.clear()
        firstSeen.clear()
    }
}
