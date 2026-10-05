package com.paulchibamba.margin.domain.rollup

import com.paulchibamba.margin.domain.feed.CandidateSource
import com.paulchibamba.margin.domain.memory.Rating
import com.paulchibamba.margin.domain.model.ConceptId
import com.paulchibamba.margin.domain.model.Format
import com.paulchibamba.margin.domain.model.NoteId
import com.paulchibamba.margin.domain.model.PostId
import com.paulchibamba.margin.domain.tracking.Event
import com.paulchibamba.margin.domain.tracking.LoggedEvent
import com.paulchibamba.margin.domain.tracking.NoteOpenVia
import com.paulchibamba.margin.domain.tracking.SessionEndReason
import com.paulchibamba.margin.domain.tracking.SessionEntry
import com.paulchibamba.margin.domain.tracking.SessionId
import java.time.Instant
import java.time.ZoneOffset
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

object RollupFixture {
    val factPost = PostId("appsec/ch1/least-privilege/fact-1")
    val quizPost = PostId("appsec/ch1/cia/mcq-1")
    val tipPost = PostId("appsec/ch1/threats/fact-2")
    val leastPrivilege = ConceptId("appsec/least-privilege")
    val readNote = NoteId("appsec/ch1/n01")
    val glancedNote = NoteId("appsec/ch1/n02")
    val skimmedNote = NoteId("grokking/ch2/n05")

    private val morning = Session("a", Instant.parse("2026-10-04T08:00:00Z"))
    private val evening = Session("b", Instant.parse("2026-10-04T20:00:00Z"))

    val day: DayEvents
        get() = DayEvents(
            logged = morningSession() + eveningSession(),
            zone = ZoneOffset.UTC,
            noteReadingTimes = mapOf(glancedNote to 60.seconds, skimmedNote to 40.seconds),
        )

    private fun morningSession(): List<LoggedEvent> = morning.events(
        Event.SessionStart(SessionEntry.LAUNCHER),
        impression(factPost, leastPrivilege, Format.FACT, step = 1),
        exposure(factPost, active = 1000.milliseconds, expected = 4000.milliseconds),
        impression(quizPost, ConceptId("appsec/cia"), Format.MCQ, step = 2),
        Event.PostAnswer(quizPost, isCorrect = true, 3000.milliseconds, Rating.GOOD, openedSourceFirst = false),
        exposure(quizPost, active = 5000.milliseconds, expected = 5000.milliseconds, idle = 2000.milliseconds),
        Event.PostRevisit(factPost, fromStep = 2, toStep = 1),
        impression(factPost, leastPrivilege, Format.FACT, step = 1, isRevisit = true),
        exposure(factPost, active = 3600.milliseconds, expected = 4000.milliseconds),
        Event.NoteOpen(readNote, NoteOpenVia.FEED_READ, openCount = 1),
        Event.ImageZoom(postId = null, noteId = readNote),
        Event.ImageZoom(postId = null, noteId = readNote),
        Event.NoteExposure(readNote, 60.seconds, 10.seconds, 200, 200, 100, scrollBacks = 2, isMarkedRead = true),
        impression(tipPost, ConceptId("appsec/threats"), Format.FACT, step = 3),
        exposure(tipPost, active = 2000.milliseconds, expected = 4000.milliseconds, exitedSession = true),
        Event.SessionEnd(SessionEndReason.BACKGROUND, 10.minutes, posts = 4, notes = 1),
    )

    private fun eveningSession(): List<LoggedEvent> = evening.events(
        Event.SessionStart(SessionEntry.DUE_NOTIFICATION),
        Event.NoteOpen(glancedNote, NoteOpenVia.CHAPTER, openCount = 3),
        Event.NoteExposure(glancedNote, 5.seconds, Duration.ZERO, 300, 3600, 10, scrollBacks = 0, isMarkedRead = false),
        Event.NoteOpen(skimmedNote, NoteOpenVia.NEXT, openCount = 1),
        Event.NoteExposure(skimmedNote, 30.seconds, Duration.ZERO, 150, 300, 80, scrollBacks = 0, isMarkedRead = false),
        Event.PostAnswer(PostId("grokking/ch2/x/recall-1"), null, 7000.milliseconds, Rating.AGAIN, false),
        Event.SessionEnd(SessionEndReason.IDLE, 4.minutes, posts = 0, notes = 0),
    )

    private fun impression(post: PostId, concept: ConceptId, format: Format, step: Int, isRevisit: Boolean = false) =
        Event.PostImpression(post, concept, format, "Paper", CandidateSource.NEW, step, isRevisit)

    private fun exposure(
        post: PostId,
        active: Duration,
        expected: Duration,
        idle: Duration = Duration.ZERO,
        exitedSession: Boolean = false,
    ) = Event.PostExposure(post, active, idle, words = 20, expected, exitedSession)

    private class Session(val id: String, val startedAt: Instant) {
        fun events(vararg events: Event): List<LoggedEvent> = events.mapIndexed { index, event ->
            LoggedEvent(startedAt.plusSeconds(index * 10L), SessionId(id), event)
        }
    }
}
