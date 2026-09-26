package com.paulchibamba.margin.data.pack

import com.paulchibamba.margin.data.database.PackContent
import com.paulchibamba.margin.data.database.entity.BookEntity
import com.paulchibamba.margin.data.database.entity.BookSettingsEntity
import com.paulchibamba.margin.data.database.entity.ChapterEntity
import com.paulchibamba.margin.data.database.entity.ConceptEntity
import com.paulchibamba.margin.data.database.entity.NoteEntity
import com.paulchibamba.margin.data.database.entity.PostEntity
import com.paulchibamba.margin.data.database.entity.ReadingOnlyChapterEntity
import com.paulchibamba.margin.domain.model.NoteId

class PackMapper {

    fun map(pack: PackFiles): MappedPack = MappedPack(
        version = pack.manifest.version,
        content = contentOf(pack.books),
        defaultBookSettings = pack.books.map { book -> defaultSettingsOf(book.slug, pack.library) },
        defaultReadingOnlyChapters = readingOnlyChaptersOf(pack.library),
        warnings = warningsFor(pack.books.flatMap(BookFileDto::concepts)),
    )

    private fun contentOf(books: List<BookFileDto>) = PackContent(
        books = books.mapIndexed { position, book -> BookEntity(book.slug, book.title, position) },
        chapters = books.flatMap { book -> book.chapters.map { ChapterEntity(book.slug, it.index, it.title) } },
        concepts = books.flatMap { book -> book.concepts.map { concept -> conceptEntity(book.slug, concept) } },
        posts = books.flatMap(BookFileDto::concepts).flatMap(::postEntities),
        notes = books.flatMap { book -> book.notes.map { note -> noteEntity(book.slug, note) } },
    )

    private fun conceptEntity(bookSlug: String, concept: ConceptDto): ConceptEntity {
        val notePosition = concept.noteId?.let(::NoteId)?.position()
        return ConceptEntity(
            id = concept.id,
            bookSlug = bookSlug,
            chapter = concept.chapter,
            order = concept.order,
            title = concept.title,
            summary = concept.summary,
            section = concept.section,
            noteId = concept.noteId,
            noteChapter = notePosition?.chapter,
            noteOrder = notePosition?.order,
        )
    }

    private fun postEntities(concept: ConceptDto): List<PostEntity> = concept.posts.mapIndexed { position, post ->
        PostEntity(
            id = requireNotNull(post.id) { "A post of ${concept.id} has no id" },
            conceptId = concept.id,
            format = requireNotNull(post.format) { "Post ${post.id} has no format" },
            role = requireNotNull(post.role) { "Post ${post.id} has no role" },
            position = position,
            json = PackJson.encodeToString(PostDto.serializer(), post),
        )
    }

    private fun noteEntity(bookSlug: String, note: NoteDto) = NoteEntity(
        id = note.id,
        bookSlug = bookSlug,
        chapter = note.chapter,
        order = note.order,
        section = note.section,
        part = note.part,
        parts = note.parts,
        html = note.html,
        words = note.words,
        minutes = note.minutes,
    )

    private fun defaultSettingsOf(bookSlug: String, library: LibraryDto): BookSettingsEntity {
        val active = library.active.firstOrNull { it.slug == bookSlug }
        return BookSettingsEntity(bookSlug, active = active != null, priority = active?.priority ?: DEFAULT_PRIORITY)
    }

    private fun readingOnlyChaptersOf(library: LibraryDto): List<ReadingOnlyChapterEntity> =
        library.readingOnly.flatMap { (bookSlug, chapters) -> chapters.map { ReadingOnlyChapterEntity(bookSlug, it) } }

    private fun warningsFor(concepts: List<ConceptDto>) = PackWarnings(
        conceptsWithoutTeachPost = concepts.filterNot { it.hasPostWithRole(TEACH) }.map(ConceptDto::id),
        conceptsWithoutTestPost = concepts.filterNot { it.hasPostWithRole(TEST) }.map(ConceptDto::id),
    )

    private fun ConceptDto.hasPostWithRole(role: String): Boolean = posts.any { it.role == role }

    private companion object {
        const val DEFAULT_PRIORITY = "normal"
        const val TEACH = "teach"
        const val TEST = "test"
    }
}
