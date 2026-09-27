package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.model.Concept
import com.paulchibamba.margin.domain.model.NoteId
import com.paulchibamba.margin.domain.model.Post
import com.paulchibamba.margin.domain.model.PostContent
import com.paulchibamba.margin.domain.repository.ContentRepository
import com.paulchibamba.margin.domain.repository.ProgressRepository
import com.paulchibamba.margin.domain.repository.SettingsRepository
import com.paulchibamba.margin.domain.rewards.BookCompletionCalculator
import javax.inject.Inject

class DescribePost @Inject constructor(
    private val content: ContentRepository,
    private val progress: ProgressRepository,
    private val settings: SettingsRepository,
) {
    suspend operator fun invoke(post: Post): PostContext {
        val concepts = content.concepts()
        val concept = concepts.first { it.id == post.conceptId }
        val calculator = BookCompletionCalculator(settings.readingOnlyChapters())
        val conceptProgress = progress.loadFeedState()?.conceptProgress.orEmpty()
        return PostContext(
            conceptTitle = concept.title,
            bookTitle = content.books().firstOrNull { it.slug == post.bookSlug }?.title.orEmpty(),
            chapterNumber = concept.chapter,
            chapterTitle = chapterTitleOf(concept),
            completion = calculator.completionOf(post.bookSlug, concepts, conceptProgress),
            sourceNote = sourceNoteOf(post, concept),
        )
    }

    private suspend fun chapterTitleOf(concept: Concept): String = content.chapters()
        .firstOrNull { it.bookSlug == concept.bookSlug && it.number == concept.chapter }
        ?.title.orEmpty()

    private fun sourceNoteOf(post: Post, concept: Concept): NoteId? =
        (post.content as? PostContent.Source)?.noteId ?: concept.sourceNoteId
}
