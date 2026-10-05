package com.paulchibamba.margin.domain.progress

import com.paulchibamba.margin.domain.model.BookSlug
import com.paulchibamba.margin.domain.model.ConceptId
import com.paulchibamba.margin.domain.model.NoteId
import com.paulchibamba.margin.domain.model.Post
import com.paulchibamba.margin.domain.model.PostContent
import com.paulchibamba.margin.domain.model.PostId
import java.time.Duration
import java.time.Instant

data class GeneratedPost(
    val kind: RewardKind,
    val conceptIds: List<ConceptId>,
    val noteId: NoteId?,
    val title: String,
    val body: String,
    val sourceLine: String?,
    val facts: Map<String, String>,
    val factsHash: String,
    val writer: String,
    val createdAt: Instant,
    val expiresAt: Instant?,
    val shownAt: Instant? = null,
    val isLessPressed: Boolean = false,
) {
    val id: PostId
        get() = idOf(kind, factsHash)

    val isShown: Boolean
        get() = shownAt != null

    fun toPastReward() = PastReward(kind, conceptIds, noteId, facts, title, createdAt, shownAt)

    fun toPost(book: BookSlug): Post? {
        val concept = conceptIds.firstOrNull() ?: return null
        return Post(id, concept, book, PostContent.Progress(kind, title, body, sourceLine, noteId))
    }

    companion object {
        const val TEMPLATE_WRITER = "template"
        private const val ID_PREFIX = "gen"
        private val UNSEEN_LIFETIME: Duration = Duration.ofDays(7)

        fun idOf(kind: RewardKind, factsHash: String) = PostId("$ID_PREFIX/${kind.key}/$factsHash")

        fun isGenerated(post: PostId): Boolean = post.value.startsWith("$ID_PREFIX/")

        fun kindOf(post: PostId): RewardKind? =
            if (isGenerated(post)) RewardKind.fromKey(post.value.split('/')[1]) else null

        fun from(seed: RewardSeed, draft: RewardDraft, writer: String, now: Instant) = GeneratedPost(
            kind = seed.kind,
            conceptIds = seed.conceptIds,
            noteId = seed.noteId,
            title = draft.title,
            body = draft.body,
            sourceLine = draft.sourceLine,
            facts = seed.facts,
            factsHash = seed.factsHash,
            writer = writer,
            createdAt = now,
            expiresAt = if (seed.kind.expiresOnEvent) null else now.plus(UNSEEN_LIFETIME),
        )
    }
}
