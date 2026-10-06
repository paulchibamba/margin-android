package com.paulchibamba.margin.data.tracking

import com.paulchibamba.margin.data.repository.mapper.FormatNames
import com.paulchibamba.margin.domain.tracking.Event
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonObjectBuilder
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

object EventProps {

    fun subjectOf(event: Event): String? = when (event) {
        is Event.SessionStart, is Event.SessionEnd, is Event.SettingChanged, is Event.DropEvent -> null
        is Event.PostImpression -> event.postId.value
        is Event.PostExposure -> event.postId.value
        is Event.PostRevisit -> event.postId.value
        is Event.PostInteraction -> event.postId.value
        is Event.PostAnswer -> event.postId.value
        is Event.PostActionTaken -> event.postId.value
        is Event.NoteOpen -> event.noteId.value
        is Event.NoteExposure -> event.noteId.value
        is Event.ImageZoom -> event.noteId?.value ?: event.postId?.value
        is Event.RewardEvent -> event.postId.value
    }

    fun of(event: Event): JsonObject = buildJsonObject {
        when (event) {
            is Event.SessionStart -> put("entry", keyOf(event.entry))
            is Event.SessionEnd -> sessionEnd(event)
            is Event.PostImpression -> postImpression(event)
            is Event.PostExposure -> postExposure(event)
            is Event.PostRevisit -> postRevisit(event)
            is Event.PostInteraction -> postInteraction(event)
            is Event.PostAnswer -> postAnswer(event)
            is Event.PostActionTaken -> put("action", keyOf(event.action))
            is Event.NoteOpen -> noteOpen(event)
            is Event.NoteExposure -> noteExposure(event)
            is Event.ImageZoom -> put("inNote", event.isInNote)
            is Event.SettingChanged -> settingChanged(event)
            is Event.RewardEvent -> rewardEvent(event)
            is Event.DropEvent -> dropEvent(event)
        }
    }

    private fun JsonObjectBuilder.sessionEnd(event: Event.SessionEnd) {
        put("reason", keyOf(event.reason))
        put("durationMs", event.duration.inWholeMilliseconds)
        put("posts", event.posts)
        put("notes", event.notes)
    }

    private fun JsonObjectBuilder.postImpression(event: Event.PostImpression) {
        put("conceptId", event.conceptId.value)
        put("format", FormatNames.nameOf(event.format))
        put("skin", event.skinName)
        put("source", keyOf(event.source))
        put("step", event.step)
        put("isRevisit", event.isRevisit)
    }

    private fun JsonObjectBuilder.postExposure(event: Event.PostExposure) {
        put("activeMs", event.activeTime.inWholeMilliseconds)
        put("idleMs", event.idleTime.inWholeMilliseconds)
        put("words", event.words)
        put("expectedMs", event.expectedTime.inWholeMilliseconds)
        put("ratio", event.attentionRatio)
        put("exitedSession", event.exitedSession)
    }

    private fun JsonObjectBuilder.postRevisit(event: Event.PostRevisit) {
        put("fromStep", event.fromStep)
        put("toStep", event.toStep)
    }

    private fun JsonObjectBuilder.postInteraction(event: Event.PostInteraction) {
        put("kind", keyOf(event.kind))
        put("tMs", event.sinceImpression.inWholeMilliseconds)
    }

    private fun JsonObjectBuilder.postAnswer(event: Event.PostAnswer) {
        put("correct", event.isCorrect)
        put("msToAnswer", event.timeToAnswer.inWholeMilliseconds)
        put("grade", event.grade.value)
        put("openedSourceFirst", event.openedSourceFirst)
    }

    private fun JsonObjectBuilder.noteOpen(event: Event.NoteOpen) {
        put("via", keyOf(event.via))
        put("openCount", event.openCount)
    }

    private fun JsonObjectBuilder.noteExposure(event: Event.NoteExposure) {
        put("activeMs", event.activeTime.inWholeMilliseconds)
        put("idleMs", event.idleTime.inWholeMilliseconds)
        put("words", event.words)
        put("wpm", event.wordsPerMinute)
        put("maxScrollPct", event.maxScrollPercent)
        put("scrollBacks", event.scrollBacks)
        put("markedRead", event.isMarkedRead)
    }

    private fun JsonObjectBuilder.settingChanged(event: Event.SettingChanged) {
        put("key", event.key)
        put("old", event.old)
        put("new", event.new)
    }

    private fun JsonObjectBuilder.rewardEvent(event: Event.RewardEvent) {
        put("kind", event.kind)
        put("action", event.action)
    }

    private fun JsonObjectBuilder.dropEvent(event: Event.DropEvent) {
        put("stage", keyOf(event.stage))
        put("position", event.position)
        put("size", event.size)
    }

    private fun keyOf(value: Enum<*>): String = value.name.lowercase()
}
