package com.paulchibamba.margin.domain.simulation

import com.paulchibamba.margin.domain.model.Book
import com.paulchibamba.margin.domain.model.BookSettings
import com.paulchibamba.margin.domain.model.BookSlug
import com.paulchibamba.margin.domain.model.Concept
import com.paulchibamba.margin.domain.model.ConceptId
import com.paulchibamba.margin.domain.model.Note
import com.paulchibamba.margin.domain.model.NoteId
import com.paulchibamba.margin.domain.model.NotePosition
import com.paulchibamba.margin.domain.model.Post
import com.paulchibamba.margin.domain.model.PostId
import com.paulchibamba.margin.domain.model.Priority
import com.paulchibamba.margin.domain.progression.ReadingOnlyChapters
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.double
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.File
import kotlin.time.Duration.Companion.minutes

object TestPackLoader {

    private const val PATH_PROPERTY = "margin.contentPack"

    val directory: File?
        get() = System.getProperty(PATH_PROPERTY)?.let(::File)?.takeIf { File(it, "manifest.json").exists() }

    val pack: TestPack? by lazy { directory?.let(::load) }

    fun load(directory: File): TestPack {
        val bookFiles = readJson(File(directory, "manifest.json")).getValue("books").jsonArray
            .map { entry -> readJson(File(directory, entry.jsonObject.string("file"))) }
        val library = readJson(File(directory, "library.json"))
        return TestPack(
            books = bookFiles.map { Book(BookSlug(it.string("slug")), it.string("title")) },
            concepts = bookFiles.flatMap(::conceptsOf),
            posts = bookFiles.flatMap(::postsOf),
            notes = bookFiles.flatMap(::notesOf),
            bookSettings = settingsFrom(library, bookFiles.map { BookSlug(it.string("slug")) }),
            readingOnlyChapters = readingOnlyFrom(library),
            previewNotesAhead = library.int("preview_notes_ahead"),
        )
    }

    private fun readJson(file: File): JsonObject = Json.parseToJsonElement(file.readText()).jsonObject

    private fun JsonObject.objects(key: String): List<JsonObject> = getValue(key).jsonArray.map { it.jsonObject }

    private fun conceptsOf(book: JsonObject): List<Concept> = book.objects("concepts").map { concept ->
        Concept(
            id = ConceptId(concept.string("id")),
            bookSlug = BookSlug(book.string("slug")),
            chapter = concept.int("chapter"),
            order = concept.int("order"),
            title = concept.string("title"),
            summary = concept.string("summary"),
            section = concept.optionalString("section").orEmpty(),
            sourceNoteId = concept.optionalString("note_id")?.let(::NoteId),
        )
    }

    private fun postsOf(book: JsonObject): List<Post> = book.objects("concepts").flatMap { concept ->
        concept.objects("posts").map { post ->
            Post(
                id = PostId(post.string("id")),
                conceptId = ConceptId(concept.string("id")),
                bookSlug = BookSlug(book.string("slug")),
                content = PackPostContent.from(post),
            )
        }
    }

    private fun notesOf(book: JsonObject): List<Note> = book.objects("notes").map { note ->
        Note(
            id = NoteId(note.string("id")),
            bookSlug = BookSlug(book.string("slug")),
            position = NotePosition(note.int("chapter"), note.int("order")),
            section = note.optionalString("section").orEmpty(),
            part = note.int("part"),
            partCount = note.int("parts"),
            html = note.string("html"),
            wordCount = note.int("words"),
            readingTime = note.getValue("minutes").jsonPrimitive.double.minutes,
        )
    }

    private fun settingsFrom(library: JsonObject, books: List<BookSlug>): List<BookSettings> {
        val priorities = library.objects("active").associate { entry ->
            BookSlug(entry.string("slug")) to Priority.valueOf(entry.string("priority").uppercase())
        }
        return books.map { book ->
            BookSettings(book, isActive = book in priorities, priority = priorities[book] ?: Priority.NORMAL)
        }
    }

    private fun readingOnlyFrom(library: JsonObject): ReadingOnlyChapters {
        val chapters = library.getValue("reading_only").jsonObject.mapValues { (_, list) ->
            list.jsonArray.map { it.jsonPrimitive.int }.toSet()
        }
        return ReadingOnlyChapters(chapters.mapKeys { (slug, _) -> BookSlug(slug) })
    }
}
