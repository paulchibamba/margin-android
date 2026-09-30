package com.paulchibamba.margin.data.repository

import com.paulchibamba.margin.data.database.DatabaseTest
import com.paulchibamba.margin.data.database.MetaKey
import com.paulchibamba.margin.data.database.entity.MetaEntity
import com.paulchibamba.margin.domain.actions.ActionLogEntry
import com.paulchibamba.margin.domain.actions.PostAction
import com.paulchibamba.margin.domain.feed.CandidateSource
import com.paulchibamba.margin.domain.feed.ConceptProgress
import com.paulchibamba.margin.domain.feed.Confidence
import com.paulchibamba.margin.domain.feed.FeedHistoryEntry
import com.paulchibamba.margin.domain.feed.FeedState
import com.paulchibamba.margin.domain.feed.ReviewLogEntry
import com.paulchibamba.margin.domain.memory.CardState
import com.paulchibamba.margin.domain.memory.MemoryCard
import com.paulchibamba.margin.domain.memory.Rating
import com.paulchibamba.margin.domain.model.BookSlug
import com.paulchibamba.margin.domain.model.ChapterRef
import com.paulchibamba.margin.domain.model.ConceptId
import com.paulchibamba.margin.domain.model.Format
import com.paulchibamba.margin.domain.model.NoteId
import com.paulchibamba.margin.domain.model.PostId
import com.paulchibamba.margin.domain.rewards.Badge
import com.paulchibamba.margin.domain.rewards.BadgeKind
import com.paulchibamba.margin.domain.rewards.DailyActivity
import com.paulchibamba.margin.domain.signals.AnswerOutcome
import com.paulchibamba.margin.domain.signals.FormatAffinity
import com.paulchibamba.margin.domain.signals.PostExit
import java.time.Instant
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class RoomProgressRepositoryTest : DatabaseTest() {

    private val repository by lazy { RoomProgressRepository(database) }
    private val now = Instant.parse("2026-10-01T08:00:00.123Z")
    private val book = BookSlug("alice-bob-appsec")
    private val cia = ConceptId("alice-bob-appsec/ch01/47d8a198")
    private val leastPrivilege = ConceptId("alice-bob-appsec/ch01/e8560034")
    private val tip = PostId("${cia.value}/tip")
    private val quiz = PostId("${cia.value}/mcq")

    @Test
    fun `nothing saved loads as no feed state`() = runTest {
        assertNull(repository.loadFeedState())
    }

    @Test
    fun `a saved feed state reloads equal`() = runTest {
        val state = feedState()

        repository.saveFeedState(state, now)

        assertEquals(state, repository.loadFeedState())
    }

    @Test
    fun `saving again keeps equal state and counts how often a post was shown`() = runTest {
        repository.saveFeedState(feedState(), now)
        val next = feedState().let { state ->
            state.copy(
                step = 3,
                seenPosts = state.seenPosts + (tip to 3),
                history = state.history + historyEntry(3, tip, Format.TIP, CandidateSource.ANGLE),
            )
        }

        repository.saveFeedState(next, now.plusSeconds(7))

        assertEquals(next, repository.loadFeedState())
        val seen = database.feedStateDao().seen(tip.value)!!
        assertEquals(2, seen.times)
        assertEquals("angle", seen.lastSource)
    }

    @Test
    fun `an exit records dwell, engagement and correctness on the seen post`() = runTest {
        repository.saveFeedState(feedState(), now)

        repository.recordExit(quiz, PostExit(5.seconds, true, AnswerOutcome.Wrong), engagement = 0.8)

        val seen = database.feedStateDao().seen(quiz.value)!!
        assertEquals(5_000L, seen.lastDwellMs)
        assertEquals(0.8, seen.lastEngagement)
        assertEquals(false, seen.lastCorrect)
    }

    @Test
    fun `logs append and are observed in order`() = runTest {
        val action = ActionLogEntry(now, 2, tip, cia, PostAction.LOST)
        val review = ReviewLogEntry(now, cia, quiz, Rating.AGAIN, CardState.REVIEW, 3.1, 0.9, dwell = null)

        repository.appendAction(action)
        repository.appendReview(review)

        assertEquals(listOf(action), repository.observeActions().first())
        assertEquals(listOf(review), repository.observeReviews().first())
    }

    @Test
    fun `reading state tracks read notes, known chapters and the last note`() = runTest {
        val note = NoteId("alice-bob-appsec/ch01/n001")

        assertTrue(repository.markNoteRead(note, now))
        assertFalse(repository.markNoteRead(note, now))
        repository.markChapterKnown(ChapterRef(book, 3), now)

        val reading = repository.reading()
        assertEquals(setOf(note), reading.readNotes)
        assertEquals(setOf(ChapterRef(book, 3)), reading.knownChapters)
        assertEquals(note, reading.lastNote)
    }

    @Test
    fun `the last note is the one set most recently`() = runTest {
        val opened = NoteId("alice-bob-appsec/ch01/n002")
        repository.markNoteRead(NoteId("alice-bob-appsec/ch01/n001"), now)

        repository.setLastNote(opened)

        assertEquals(opened, repository.reading().lastNote)
    }

    @Test
    fun `unmarking a known chapter leaves the other known chapters`() = runTest {
        repository.markChapterKnown(ChapterRef(book, 3), now)
        repository.markChapterKnown(ChapterRef(book, 4), now)

        repository.unmarkChapterKnown(ChapterRef(book, 3))

        assertEquals(setOf(ChapterRef(book, 4)), repository.reading().knownChapters)
    }

    @Test
    fun `activity adds up per day and reports the change`() = runTest {
        val day = LocalDate.of(2026, 10, 1)

        repository.addActivity(day, postsSeen = 4, notesRead = 0)
        val change = repository.addActivity(day, postsSeen = 1, notesRead = 1)

        assertEquals(DailyActivity(day, 4, 0), change.before)
        assertEquals(DailyActivity(day, 5, 1), change.after)
        assertEquals(listOf(DailyActivity(day, 5, 1)), repository.observeActivity().first())
    }

    @Test
    fun `shown badges are remembered`() = runTest {
        val badges = setOf(Badge(book, BadgeKind.INTRODUCED), Badge(BookSlug("grokking"), BadgeKind.REMEMBERED))

        repository.markBadgesShown(badges)

        assertEquals(badges, repository.shownBadges())
    }

    @Test
    fun `clearing progress empties every progress table and keeps the settings`() = runTest {
        fillProgress()
        val settings = listOf(
            MetaEntity(MetaKey.PACK_VERSION, "7"),
            MetaEntity(MetaKey.DESIRED_RETENTION, "0.85"),
            MetaEntity(MetaKey.REVIEW_REMINDER, "true"),
            MetaEntity(MetaKey.DARK_MODE, "ALWAYS"),
            MetaEntity(MetaKey.DARK_POSTS, "true"),
        )
        database.metaDao().put(settings)
        database.settingsDao().replaceReadingOnlyChapters(book.value, listOf(10, 11))

        repository.clearProgress()

        PROGRESS_TABLES.forEach { table -> assertEquals(0, rowsIn(table), "$table still has rows") }
        assertNull(repository.loadFeedState())
        assertEquals(settings.toSet(), database.metaDao().withPrefix("").toSet())
        assertEquals(2, rowsIn("reading_only_chapter"))
    }

    private suspend fun fillProgress() {
        repository.saveFeedState(feedState(), now)
        repository.recordExit(quiz, PostExit(5.seconds, true, AnswerOutcome.Wrong), engagement = 0.8)
        repository.appendAction(ActionLogEntry(now, 2, tip, cia, PostAction.LOST))
        repository.appendReview(ReviewLogEntry(now, cia, quiz, Rating.AGAIN, CardState.REVIEW, 3.1, 0.9, dwell = null))
        repository.markNoteRead(NoteId("alice-bob-appsec/ch01/n001"), now)
        repository.markChapterKnown(ChapterRef(book, 3), now)
        repository.addActivity(LocalDate.of(2026, 10, 1), postsSeen = 4, notesRead = 1)
        repository.markBadgesShown(setOf(Badge(book, BadgeKind.INTRODUCED)))
        PROGRESS_TABLES.forEach { table -> assertTrue(rowsIn(table) > 0, "$table was not filled") }
    }

    private fun rowsIn(table: String): Int =
        database.openHelper.readableDatabase.query("SELECT COUNT(*) FROM $table").use { cursor ->
            cursor.moveToFirst()
            cursor.getInt(0)
        }

    private fun historyEntry(step: Int, post: PostId, format: Format, source: CandidateSource) =
        FeedHistoryEntry(step, post, cia, format, format.role, source)

    private fun feedState() = FeedState(
        delightAtStep = 8,
        step = 2,
        conceptProgress = mapOf(
            cia to ConceptProgress(
                introducedAtStep = 1,
                card = MemoryCard.new(now).copy(
                    state = CardState.LEARNING,
                    stability = 2.3,
                    difficulty = 5.1,
                    reps = 1,
                    lastReview = now,
                ),
                confidence = Confidence.LOST,
                isLostGraded = true,
                lostAtStep = 2,
                lastShownStep = 2,
            ),
            leastPrivilege to ConceptProgress(lastShownStep = 1),
        ),
        seenPosts = mapOf(tip to 1, quiz to 2),
        history = listOf(
            historyEntry(1, tip, Format.TIP, CandidateSource.NEW),
            historyEntry(2, quiz, Format.MCQ, CandidateSource.REVIEW),
        ),
        bookLastNewStep = mapOf(book to 1),
        lastPreviewAtStep = 1,
        affinity = FormatAffinity(mapOf(Format.TIP to 0.62, Format.CODE_EXAMPLE to 0.31)),
        savedPosts = setOf(tip),
    )

    private companion object {
        val PROGRESS_TABLES = listOf(
            "concept_progress", "review_log", "post_seen", "action_log", "format_affinity",
            "note_read", "chapter_known", "saved_post", "feed_history", "daily_activity",
        )
    }
}
