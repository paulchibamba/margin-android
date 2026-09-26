package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.feed.aiSecurity
import com.paulchibamba.margin.domain.feed.allConcepts
import com.paulchibamba.margin.domain.feed.appSec
import com.paulchibamba.margin.domain.feed.grokking
import com.paulchibamba.margin.domain.feed.postsOf
import com.paulchibamba.margin.domain.feed.sourceNoteOf
import com.paulchibamba.margin.domain.model.Book
import com.paulchibamba.margin.domain.model.Chapter
import com.paulchibamba.margin.domain.model.Concept
import com.paulchibamba.margin.domain.model.Note
import com.paulchibamba.margin.domain.model.NoteId
import com.paulchibamba.margin.domain.model.NoteOutline
import com.paulchibamba.margin.domain.model.Post
import com.paulchibamba.margin.domain.repository.ContentRepository

class FakeContentRepository(
    private val books: List<Book> = listOf(appSec, grokking, aiSecurity),
    private val concepts: List<Concept> = allConcepts,
    private val posts: List<Post> = allConcepts.flatMap(::postsOf),
    private val notes: List<Note> = allConcepts.map { sourceNoteOf(it, "<p>The book's own words.</p>") },
) : ContentRepository {

    override suspend fun books() = books

    override suspend fun chapters() = notes.map { it.bookSlug to it.position.chapter }.distinct()
        .map { (book, chapter) -> Chapter(book, chapter, "Chapter $chapter", isReadingOnly = false) }

    override suspend fun concepts() = concepts

    override suspend fun posts() = posts

    override suspend fun noteOutlines() =
        notes.map { NoteOutline(it.id, it.bookSlug, it.position, it.section, it.readingTime) }

    override suspend fun notes(ids: Collection<NoteId>) = notes.filter { it.id in ids }

    override suspend fun note(id: NoteId) = notes.firstOrNull { it.id == id }
}
