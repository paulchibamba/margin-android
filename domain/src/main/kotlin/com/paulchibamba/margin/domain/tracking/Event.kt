package com.paulchibamba.margin.domain.tracking

import com.paulchibamba.margin.domain.actions.PostAction
import com.paulchibamba.margin.domain.feed.CandidateSource
import com.paulchibamba.margin.domain.memory.Rating
import com.paulchibamba.margin.domain.model.ConceptId
import com.paulchibamba.margin.domain.model.Format
import com.paulchibamba.margin.domain.model.NoteId
import com.paulchibamba.margin.domain.model.PostId
import kotlin.time.Duration

sealed interface Event {
    val type: EventType

    data class SessionStart(val entry: SessionEntry) : Event {
        override val type get() = EventType.SESSION_START
    }

    data class SessionEnd(
        val reason: SessionEndReason,
        val duration: Duration,
        val posts: Int,
        val notes: Int,
    ) : Event {
        override val type get() = EventType.SESSION_END
    }

    data class PostImpression(
        val postId: PostId,
        val conceptId: ConceptId,
        val format: Format,
        val skinName: String,
        val source: CandidateSource,
        val step: Int,
        val isRevisit: Boolean,
    ) : Event {
        override val type get() = EventType.POST_IMPRESSION
    }

    data class PostExposure(
        val postId: PostId,
        val activeTime: Duration,
        val idleTime: Duration,
        val words: Int,
        val expectedTime: Duration,
        val exitedSession: Boolean,
    ) : Event {
        override val type get() = EventType.POST_EXPOSURE
        val attentionRatio: Double get() = activeTime / expectedTime
    }

    data class PostRevisit(val postId: PostId, val fromStep: Int, val toStep: Int) : Event {
        override val type get() = EventType.POST_REVISIT
    }

    data class PostInteraction(
        val postId: PostId,
        val kind: InteractionKind,
        val sinceImpression: Duration,
    ) : Event {
        override val type get() = EventType.POST_INTERACTION
    }

    data class PostAnswer(
        val postId: PostId,
        val isCorrect: Boolean?,
        val timeToAnswer: Duration,
        val grade: Rating,
        val openedSourceFirst: Boolean,
    ) : Event {
        override val type get() = EventType.POST_ANSWER
    }

    data class PostActionTaken(val postId: PostId, val action: PostAction) : Event {
        override val type get() = EventType.POST_ACTION
    }

    data class NoteOpen(val noteId: NoteId, val via: NoteOpenVia, val openCount: Int) : Event {
        override val type get() = EventType.NOTE_OPEN
    }

    data class NoteExposure(
        val noteId: NoteId,
        val activeTime: Duration,
        val idleTime: Duration,
        val words: Int,
        val wordsPerMinute: Int,
        val maxScrollPercent: Int,
        val scrollBacks: Int,
        val isMarkedRead: Boolean,
    ) : Event {
        override val type get() = EventType.NOTE_EXPOSURE
    }

    data class ImageZoom(val postId: PostId?, val noteId: NoteId?) : Event {
        init { require((postId == null) != (noteId == null)) { "An image zoom belongs to one post or one note" } }
        override val type get() = EventType.IMAGE_ZOOM
        val isInNote: Boolean get() = noteId != null
    }

    data class RewardEvent(val postId: PostId, val kind: String, val action: String) : Event {
        override val type get() = EventType.REWARD_EVENT
    }

    data class SettingChanged(val key: String, val old: String?, val new: String) : Event {
        override val type get() = EventType.SETTING_CHANGED
    }
}
