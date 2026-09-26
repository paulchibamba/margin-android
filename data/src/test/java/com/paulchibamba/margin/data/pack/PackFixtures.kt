package com.paulchibamba.margin.data.pack

import kotlinx.serialization.encodeToString

const val APPSEC = "alice-bob-appsec"
const val GROKKING = "grokking-web-app-security"

fun tipPost(conceptId: String) = PostDto(id = "$conceptId/tip", format = "tip", role = "teach", title = "Tip",
    slides = listOf("Validate on the server."))

fun mcqPost(conceptId: String) = PostDto(id = "$conceptId/mcq", format = "mcq", role = "test", title = "Quiz",
    question = "Which?", options = listOf("A", "B", "C", "D"), answerIndex = 1, explanation = "Because B.")

fun conceptDto(bookSlug: String, chapter: Int, order: Int, posts: (String) -> List<PostDto> = ::bothPosts): ConceptDto {
    val id = "$bookSlug/ch0$chapter/c$order"
    return ConceptDto(id, chapter, order, "Concept $order", "Summary", "Section", noteId(bookSlug, chapter, order),
        posts(id))
}

fun bothPosts(conceptId: String) = listOf(tipPost(conceptId), mcqPost(conceptId))

fun noteId(bookSlug: String, chapter: Int, order: Int) = "$bookSlug/ch0$chapter/n00$order"

fun noteDto(bookSlug: String, chapter: Int, order: Int) =
    NoteDto(noteId(bookSlug, chapter, order), chapter, order, "Section", 1, 1, "<p>Text $order</p>", 150, 0.8)

fun bookFile(slug: String, title: String = "Book $slug", concepts: List<ConceptDto> = defaultConcepts(slug)) =
    BookFileDto(
        slug = slug,
        title = title,
        chapters = listOf(ChapterDto(1, "Chapter 1"), ChapterDto(2, "Chapter 2")),
        concepts = concepts,
        notes = (0..2).map { order -> noteDto(slug, 1, order) },
    )

fun defaultConcepts(slug: String) = listOf(conceptDto(slug, 1, 0), conceptDto(slug, 1, 1))

val defaultLibrary = LibraryDto(
    active = listOf(ActiveBookDto(APPSEC, "main")),
    readingOnly = mapOf(APPSEC to listOf(2), GROKKING to listOf(1)),
)

fun packFiles(
    version: String = "v1",
    books: List<BookFileDto> = listOf(bookFile(APPSEC), bookFile(GROKKING)),
    library: LibraryDto = defaultLibrary,
) = PackFiles(
    manifest = ManifestDto(version, books.map { ManifestBookDto(it.slug, it.title, "${it.slug}.json") }),
    library = library,
    books = books,
)

fun PackFiles.asAssets(): AssetSource = MapAssetSource(
    buildMap {
        put("pack/manifest.json", PackJson.encodeToString(manifest))
        put("pack/library.json", PackJson.encodeToString(library))
        books.forEach { book -> put("pack/${book.slug}.json", PackJson.encodeToString(book)) }
    },
)
