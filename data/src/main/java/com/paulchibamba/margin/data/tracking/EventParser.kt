package com.paulchibamba.margin.data.tracking

import com.paulchibamba.margin.data.repository.mapper.FormatNames
import com.paulchibamba.margin.data.repository.mapper.SourceNames
import com.paulchibamba.margin.domain.memory.Rating
import com.paulchibamba.margin.domain.model.ConceptId
import com.paulchibamba.margin.domain.model.NoteId
import com.paulchibamba.margin.domain.model.PostId
import com.paulchibamba.margin.domain.tracking.Event
import com.paulchibamba.margin.domain.tracking.EventType
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long

object EventParser {

    fun parse(typeKey: String, subjectId: String?, props: String): Event? {
        val type = EventType.entries.firstOrNull { it.key == typeKey } ?: return null
        return runCatching { Props(Json.parseToJsonElement(props).jsonObject, subjectId).eventOf(type) }.getOrNull()
    }

    private fun Props.eventOf(type: EventType): Event = when (type) {
        EventType.SESSION_START -> Event.SessionStart(enum("entry"))
        EventType.SESSION_END -> Event.SessionEnd(enum("reason"), millis("durationMs"), int("posts"), int("notes"))
        EventType.POST_IMPRESSION -> postImpression()
        EventType.POST_EXPOSURE -> postExposure()
        EventType.POST_REVISIT -> Event.PostRevisit(post(), int("fromStep"), int("toStep"))
        EventType.POST_INTERACTION -> Event.PostInteraction(post(), enum("kind"), millis("tMs"))
        EventType.POST_ANSWER -> postAnswer()
        EventType.POST_ACTION -> Event.PostActionTaken(post(), enum("action"))
        EventType.NOTE_OPEN -> Event.NoteOpen(note(), enum("via"), int("openCount"))
        EventType.NOTE_EXPOSURE -> noteExposure()
        EventType.IMAGE_ZOOM -> imageZoom()
        EventType.SETTING_CHANGED -> Event.SettingChanged(text("key"), optionalText("old"), text("new"))
        EventType.REWARD_EVENT -> Event.RewardEvent(post(), text("kind"), text("action"))
    }

    private fun Props.postImpression() = Event.PostImpression(
        postId = post(),
        conceptId = ConceptId(text("conceptId")),
        format = FormatNames.formatOf(text("format")),
        skinName = text("skin"),
        source = SourceNames.sourceOf(text("source")),
        step = int("step"),
        isRevisit = boolean("isRevisit"),
    )

    private fun Props.postExposure() = Event.PostExposure(
        postId = post(),
        activeTime = millis("activeMs"),
        idleTime = millis("idleMs"),
        words = int("words"),
        expectedTime = millis("expectedMs"),
        exitedSession = boolean("exitedSession"),
    )

    private fun Props.postAnswer() = Event.PostAnswer(
        postId = post(),
        isCorrect = json.getValue("correct").jsonPrimitive.booleanOrNull,
        timeToAnswer = millis("msToAnswer"),
        grade = Rating.entries.first { it.value == int("grade") },
        openedSourceFirst = boolean("openedSourceFirst"),
    )

    private fun Props.noteExposure() = Event.NoteExposure(
        noteId = note(),
        activeTime = millis("activeMs"),
        idleTime = millis("idleMs"),
        words = int("words"),
        wordsPerMinute = int("wpm"),
        maxScrollPercent = int("maxScrollPct"),
        scrollBacks = int("scrollBacks"),
        isMarkedRead = boolean("markedRead"),
    )

    private fun Props.imageZoom() =
        if (boolean("inNote")) Event.ImageZoom(null, note()) else Event.ImageZoom(post(), null)

    private class Props(val json: JsonObject, private val subject: String?) {
        fun post() = PostId(requireNotNull(subject))

        fun note() = NoteId(requireNotNull(subject))

        fun text(key: String): String = json.getValue(key).jsonPrimitive.content

        fun optionalText(key: String): String? = json[key]?.jsonPrimitive?.contentOrNull

        fun int(key: String): Int = json.getValue(key).jsonPrimitive.int

        fun boolean(key: String): Boolean = json.getValue(key).jsonPrimitive.boolean

        fun millis(key: String) = json.getValue(key).jsonPrimitive.long.milliseconds

        inline fun <reified T : Enum<T>> enum(key: String): T = enumValueOf(text(key).uppercase())
    }
}
