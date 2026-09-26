package com.paulchibamba.margin.data.database

import com.paulchibamba.margin.data.database.entity.ActionLogEntity
import com.paulchibamba.margin.data.database.entity.BookEntity
import com.paulchibamba.margin.data.database.entity.BookSettingsEntity
import com.paulchibamba.margin.data.database.entity.ChapterEntity
import com.paulchibamba.margin.data.database.entity.ChapterKnownEntity
import com.paulchibamba.margin.data.database.entity.ConceptEntity
import com.paulchibamba.margin.data.database.entity.ConceptProgressEntity
import com.paulchibamba.margin.data.database.entity.DailyActivityEntity
import com.paulchibamba.margin.data.database.entity.FeedHistoryEntity
import com.paulchibamba.margin.data.database.entity.FormatAffinityEntity
import com.paulchibamba.margin.data.database.entity.MetaEntity
import com.paulchibamba.margin.data.database.entity.NoteEntity
import com.paulchibamba.margin.data.database.entity.NoteReadEntity
import com.paulchibamba.margin.data.database.entity.PostEntity
import com.paulchibamba.margin.data.database.entity.PostSeenEntity
import com.paulchibamba.margin.data.database.entity.ReviewLogEntity
import com.paulchibamba.margin.data.database.entity.SavedPostEntity

const val BOOK = "alice-bob-appsec"
const val CONCEPT = "alice-bob-appsec/ch01/47d8a198"
const val POST = "alice-bob-appsec/ch01/47d8a198/a1b2c3"
const val NOTE = "alice-bob-appsec/ch01/n001"

fun packContent(title: String = "Alice and Bob Learn Application Security") = PackContent(
    books = listOf(BookEntity(BOOK, title, position = 0)),
    chapters = listOf(ChapterEntity(BOOK, number = 1, title = "Security Fundamentals")),
    concepts = listOf(conceptEntity()),
    posts = listOf(PostEntity(POST, CONCEPT, "tip", "teach", position = 0, json = """{"format":"tip"}""")),
    notes = listOf(noteEntity()),
)

fun conceptEntity(id: String = CONCEPT, chapter: Int = 1, order: Int = 0) = ConceptEntity(
    id = id,
    bookSlug = BOOK,
    chapter = chapter,
    order = order,
    title = "The CIA Triad",
    summary = "Confidentiality, integrity and availability.",
    section = "The Security Mandate",
    noteId = NOTE,
    noteChapter = 1,
    noteOrder = 1,
)

fun noteEntity(id: String = NOTE, chapter: Int = 1, order: Int = 1) = NoteEntity(
    id = id,
    bookSlug = BOOK,
    chapter = chapter,
    order = order,
    section = "The Security Mandate",
    part = 1,
    parts = 1,
    html = "<p>Text</p>",
    words = 180,
    minutes = 0.9,
)

fun conceptProgress(conceptId: String = CONCEPT) = ConceptProgressEntity(
    conceptId = conceptId,
    introducedAtStep = 3,
    confidence = "lost",
    fsrsDue = 1_790_000_000_000,
    fsrsStability = 2.3,
    fsrsDifficulty = 5.1,
    fsrsState = 2,
    fsrsReps = 4,
    fsrsLapses = 1,
    fsrsScheduledDays = 3,
    fsrsLearningSteps = 0,
    fsrsLastReview = 1_789_000_000_000,
    lostGraded = true,
    lostAtStep = 9,
    retaughtAtStep = null,
    lastShownStep = 12,
)

fun actionLog() = ActionLogEntity(at = 1_000, step = 4, postId = POST, conceptId = CONCEPT, action = "got")

fun reviewLog() = ReviewLogEntity(
    conceptId = CONCEPT,
    postId = POST,
    at = 2_000,
    rating = 3,
    stateBefore = 0,
    stabilityBefore = 0.0,
    stabilityAfter = 2.3,
    dwellMs = 5_000,
)

suspend fun MarginDatabase.fillProgressTables() {
    conceptProgressDao().upsert(listOf(conceptProgress()))
    feedLogDao().insertAction(actionLog())
    feedLogDao().insertReview(reviewLog())
    feedStateDao().upsertSeen(listOf(PostSeenEntity(POST, 1_000, 2_000, 5, 2, 5_000, 0.8, true, "new")))
    feedStateDao().upsertAffinity(listOf(FormatAffinityEntity("tip", 0.62)))
    feedStateDao().upsertHistory(listOf(FeedHistoryEntity(5, POST, CONCEPT, "tip", "teach", "new")))
    feedStateDao().insertSaved(SavedPostEntity(POST, savedAt = 3_000))
    readingDao().markNoteRead(NoteReadEntity(NOTE, readAt = 4_000))
    readingDao().markChapterKnown(ChapterKnownEntity(BOOK, chapter = 2, markedAt = 5_000))
    settingsDao().upsertBookSettings(BookSettingsEntity(BOOK, active = true, priority = "main"))
    settingsDao().replaceReadingOnlyChapters(BOOK, listOf(10, 11))
    metaDao().put(listOf(MetaEntity(MetaKey.FEED_STEP, "12")))
    activityDao().upsert(DailyActivityEntity("2026-10-01", postsSeen = 6, notesRead = 1))
}

fun bookSettings(active: Boolean, priority: String) =
    BookSettingsEntity(BOOK, active, priority)

fun meta(key: String, value: String) = MetaEntity(key, value)

fun activity(date: String, postsSeen: Int) =
    DailyActivityEntity(date, postsSeen, notesRead = 0)
