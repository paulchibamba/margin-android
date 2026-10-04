package com.paulchibamba.margin.domain.tracking

import com.paulchibamba.margin.domain.actions.PostAction
import com.paulchibamba.margin.domain.feed.CandidateSource
import com.paulchibamba.margin.domain.memory.Rating
import com.paulchibamba.margin.domain.model.ConceptId
import com.paulchibamba.margin.domain.model.Format
import com.paulchibamba.margin.domain.model.NoteId
import com.paulchibamba.margin.domain.model.PostId
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

object EventFixtures {
    val post = PostId("appsec/ch1/least-privilege/fact-1")
    val concept = ConceptId("appsec/least-privilege")
    val note = NoteId("appsec/ch1/n03")

    fun all(): List<Event> = EventType.entries.map(::forType)

    fun forType(type: EventType): Event = when (type) {
        EventType.SESSION_START -> Event.SessionStart(SessionEntry.DUE_NOTIFICATION)
        EventType.SESSION_END -> Event.SessionEnd(SessionEndReason.IDLE, 95.seconds, posts = 12, notes = 1)
        EventType.POST_IMPRESSION ->
            Event.PostImpression(post, concept, Format.FACT, "Paper", CandidateSource.NEW, step = 41, isRevisit = false)
        EventType.POST_EXPOSURE ->
            Event.PostExposure(post, 4200.milliseconds, 900.milliseconds, 31, 7750.milliseconds, exitedSession = false)
        EventType.POST_REVISIT -> Event.PostRevisit(post, fromStep = 42, toStep = 41)
        EventType.POST_INTERACTION -> Event.PostInteraction(post, InteractionKind.REVEAL, 1300.milliseconds)
        EventType.POST_ANSWER -> Event.PostAnswer(post, isCorrect = true, 3100.milliseconds, Rating.GOOD, false)
        EventType.POST_ACTION -> Event.PostActionTaken(post, PostAction.SAVE)
        EventType.NOTE_OPEN -> Event.NoteOpen(note, NoteOpenVia.FEED_READ, openCount = 2)
        EventType.NOTE_EXPOSURE ->
            Event.NoteExposure(note, 80.seconds, 12.seconds, 410, 240, 100, scrollBacks = 1, isMarkedRead = true)
        EventType.IMAGE_ZOOM -> Event.ImageZoom(postId = null, noteId = note)
        EventType.SETTING_CHANGED -> Event.SettingChanged("dark_mode", old = "FOLLOW_SYSTEM", new = "ALWAYS")
    }
}
