package com.paulchibamba.margin.data.repository.mapper

import com.paulchibamba.margin.data.database.NoteOutlineRow
import com.paulchibamba.margin.data.database.entity.BookEntity
import com.paulchibamba.margin.data.database.entity.ChapterEntity
import com.paulchibamba.margin.data.database.entity.ConceptEntity
import com.paulchibamba.margin.data.database.entity.NoteEntity
import com.paulchibamba.margin.data.database.entity.PostEntity
import com.paulchibamba.margin.domain.model.Book
import com.paulchibamba.margin.domain.model.BookSlug
import com.paulchibamba.margin.domain.model.Chapter
import com.paulchibamba.margin.domain.model.Concept
import com.paulchibamba.margin.domain.model.ConceptId
import com.paulchibamba.margin.domain.model.Note
import com.paulchibamba.margin.domain.model.NoteId
import com.paulchibamba.margin.domain.model.NoteOutline
import com.paulchibamba.margin.domain.model.NotePosition
import com.paulchibamba.margin.domain.model.Post
import com.paulchibamba.margin.domain.model.PostId
import kotlin.time.Duration.Companion.minutes

object ContentMapper {

    fun book(entity: BookEntity) = Book(BookSlug(entity.slug), entity.title)

    fun chapter(entity: ChapterEntity, isReadingOnly: Boolean) =
        Chapter(BookSlug(entity.bookSlug), entity.number, entity.title, isReadingOnly)

    fun concept(entity: ConceptEntity) = Concept(
        id = ConceptId(entity.id),
        bookSlug = BookSlug(entity.bookSlug),
        chapter = entity.chapter,
        order = entity.order,
        title = entity.title,
        summary = entity.summary,
        section = entity.section,
        sourceNoteId = entity.noteId?.let(::NoteId),
    )

    fun post(entity: PostEntity, bookSlug: BookSlug) = Post(
        id = PostId(entity.id),
        conceptId = ConceptId(entity.conceptId),
        bookSlug = bookSlug,
        content = PostContentMapper.fromJson(entity.json),
    )

    fun note(entity: NoteEntity) = Note(
        id = NoteId(entity.id),
        bookSlug = BookSlug(entity.bookSlug),
        position = NotePosition(entity.chapter, entity.order),
        section = entity.section,
        part = entity.part,
        partCount = entity.parts,
        html = entity.html,
        wordCount = entity.words,
        readingTime = entity.minutes.minutes,
    )

    fun outline(row: NoteOutlineRow) = NoteOutline(
        id = NoteId(row.id),
        bookSlug = BookSlug(row.bookSlug),
        position = NotePosition(row.chapter, row.order),
        section = row.section,
        readingTime = row.minutes.minutes,
    )
}
