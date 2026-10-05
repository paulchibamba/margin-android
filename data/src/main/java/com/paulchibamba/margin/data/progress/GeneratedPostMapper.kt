package com.paulchibamba.margin.data.progress

import com.paulchibamba.margin.data.database.entity.GeneratedPostEntity
import com.paulchibamba.margin.domain.model.ConceptId
import com.paulchibamba.margin.domain.model.NoteId
import com.paulchibamba.margin.domain.progress.GeneratedPost
import com.paulchibamba.margin.domain.progress.RewardKind
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import java.time.Instant

internal object GeneratedPostMapper {
    private val idsSerializer = ListSerializer(String.serializer())
    private val factsSerializer = MapSerializer(String.serializer(), String.serializer())

    fun toEntity(post: GeneratedPost) = GeneratedPostEntity(
        id = post.id.value,
        kind = post.kind.key,
        conceptIds = Json.encodeToString(idsSerializer, post.conceptIds.map(ConceptId::value)),
        noteId = post.noteId?.value,
        title = post.title,
        body = post.body,
        sourceLine = post.sourceLine,
        factsJson = Json.encodeToString(factsSerializer, post.facts),
        factsHash = post.factsHash,
        writer = post.writer,
        createdAt = post.createdAt.toEpochMilli(),
        expiresAt = post.expiresAt?.toEpochMilli(),
        shownAt = post.shownAt?.toEpochMilli(),
        lessPressed = post.isLessPressed,
    )

    fun toDomain(entity: GeneratedPostEntity): GeneratedPost? {
        val kind = RewardKind.fromKey(entity.kind) ?: return null
        return GeneratedPost(
            kind = kind,
            conceptIds = Json.decodeFromString(idsSerializer, entity.conceptIds).map(::ConceptId),
            noteId = entity.noteId?.let(::NoteId),
            title = entity.title,
            body = entity.body,
            sourceLine = entity.sourceLine,
            facts = Json.decodeFromString(factsSerializer, entity.factsJson),
            factsHash = entity.factsHash,
            writer = entity.writer,
            createdAt = Instant.ofEpochMilli(entity.createdAt),
            expiresAt = entity.expiresAt?.let(Instant::ofEpochMilli),
            shownAt = entity.shownAt?.let(Instant::ofEpochMilli),
            isLessPressed = entity.lessPressed,
        )
    }
}
