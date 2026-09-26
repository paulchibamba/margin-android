package com.paulchibamba.margin.domain.feed

import com.paulchibamba.margin.domain.model.Book
import com.paulchibamba.margin.domain.model.BookSettings
import com.paulchibamba.margin.domain.model.BookSlug
import com.paulchibamba.margin.domain.model.Concept
import com.paulchibamba.margin.domain.model.Format
import com.paulchibamba.margin.domain.model.Note
import com.paulchibamba.margin.domain.model.NoteId
import com.paulchibamba.margin.domain.model.Post
import com.paulchibamba.margin.domain.model.PostContent
import com.paulchibamba.margin.domain.model.PostId
import com.paulchibamba.margin.domain.model.PostRole
import com.paulchibamba.margin.domain.progression.PreviewWindow
import com.paulchibamba.margin.domain.progression.ReadingOnlyChapters
import com.paulchibamba.margin.domain.progression.ReadingProgress
import com.paulchibamba.margin.domain.progression.UnlockRule

class LearningLibrary(
    val books: List<Book>,
    concepts: List<Concept>,
    posts: List<Post>,
    private val sourceNotes: Map<NoteId, Note>,
    val bookSettings: List<BookSettings>,
    private val readingOnlyChapters: ReadingOnlyChapters,
    readingProgress: ReadingProgress,
    private val previewWindow: PreviewWindow = PreviewWindow(),
) {
    private val unlockRule = UnlockRule(readingOnlyChapters)
    private val postsByConcept = posts.groupBy(Post::conceptId)
    private val conceptsByBook = concepts
        .groupBy(Concept::bookSlug)
        .mapValues { (_, bookConcepts) -> inBookOrder(bookConcepts) }
    private val activeBookSlugs = bookSettings.filter(BookSettings::isActive).map(BookSettings::bookSlug).toSet()
    private val frontiers = books.associate { book -> book.slug to readingProgress.frontierOf(book.slug) }

    val conceptsInBookOrder: List<Concept> = books.flatMap { book -> conceptsOf(book.slug) }

    val activeBooks: List<Book> = books.filter { book -> isActive(book.slug) }

    fun conceptsOf(book: BookSlug): List<Concept> = conceptsByBook[book].orEmpty()

    fun isActive(book: BookSlug): Boolean = book in activeBookSlugs

    fun isReadingOnly(concept: Concept): Boolean = concept in readingOnlyChapters

    fun isUnlocked(concept: Concept): Boolean = unlockRule.isUnlocked(concept, frontiers[concept.bookSlug])

    fun isInPreviewWindow(concept: Concept): Boolean =
        previewWindow.contains(concept.position, frontiers[concept.bookSlug])

    fun postsOf(concept: Concept): List<Post> = postsByConcept[concept.id].orEmpty()

    fun teachPostsOf(concept: Concept): List<Post> = postsOf(concept).filter { it.role == PostRole.TEACH && !it.isMeme }

    fun testPostsOf(concept: Concept): List<Post> = postsOf(concept).filter { it.role == PostRole.TEST }

    fun memesOf(concept: Concept): List<Post> = postsOf(concept).filter { it.isMeme }

    fun nonMemePostsOf(concept: Concept): List<Post> = postsOf(concept).filterNot { it.isMeme }

    fun sourcePostFor(concept: Concept): Post? {
        val note = concept.sourceNoteId?.let(sourceNotes::get) ?: return null
        val excerpt = NoteExcerpt.of(note.html).ifBlank { return null }
        return Post(
            id = PostId("${concept.id.value}$SOURCE_POST_SUFFIX"),
            conceptId = concept.id,
            bookSlug = concept.bookSlug,
            content = PostContent.Source(
                title = note.section.ifBlank { concept.title },
                excerpt = excerpt,
                noteId = note.id,
            ),
        )
    }

    private val Post.isMeme: Boolean
        get() = format == Format.MEME

    private fun inBookOrder(concepts: List<Concept>): List<Concept> =
        concepts.sortedWith(compareBy(Concept::chapter, Concept::order))

    private companion object {
        const val SOURCE_POST_SUFFIX = "/source"
    }
}
