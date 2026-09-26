package com.paulchibamba.margin.domain.feed

import com.paulchibamba.margin.domain.memory.MemoryCard
import com.paulchibamba.margin.domain.model.Book
import com.paulchibamba.margin.domain.model.BookSettings
import com.paulchibamba.margin.domain.model.BookSlug
import com.paulchibamba.margin.domain.model.Concept
import com.paulchibamba.margin.domain.model.ConceptId
import com.paulchibamba.margin.domain.model.Note
import com.paulchibamba.margin.domain.model.NoteId
import com.paulchibamba.margin.domain.model.Post
import com.paulchibamba.margin.domain.model.PostContent
import com.paulchibamba.margin.domain.model.PostId
import com.paulchibamba.margin.domain.model.Priority
import com.paulchibamba.margin.domain.progression.ReadingOnlyChapters
import com.paulchibamba.margin.domain.progression.ReadingProgress
import java.time.Instant
import kotlin.random.Random
import kotlin.time.Duration.Companion.minutes

val now: Instant = Instant.parse("2026-10-01T08:00:00Z")

val appSec = Book(BookSlug("alice-bob-appsec"), "Alice and Bob Learn Application Security")
val grokking = Book(BookSlug("grokking-web-app-security"), "Grokking Web Application Security")
val aiSecurity = Book(BookSlug("practical-ai-security"), "Practical AI Security")

fun noteId(book: Book, chapter: Int, order: Int) =
    NoteId("${book.slug.value}/ch${chapter.toString().padStart(2, '0')}/n${order.toString().padStart(3, '0')}")

fun conceptOf(book: Book, chapter: Int, order: Int) = Concept(
    id = ConceptId("${book.slug.value}/ch$chapter/c$order"),
    bookSlug = book.slug,
    chapter = chapter,
    order = order,
    title = "Concept ${book.slug.value} $chapter.$order",
    summary = "",
    section = "",
    sourceNoteId = noteId(book, chapter, order),
)

val cia = conceptOf(appSec, chapter = 1, order = 0)
val leastPrivilege = conceptOf(appSec, chapter = 1, order = 1)
val readingOnlyIntro = conceptOf(appSec, chapter = 2, order = 0)
val defenceInDepth = conceptOf(appSec, chapter = 3, order = 0)
val sameOrigin = conceptOf(grokking, chapter = 1, order = 0)
val promptInjection = conceptOf(aiSecurity, chapter = 1, order = 0)

val allConcepts = listOf(cia, leastPrivilege, readingOnlyIntro, defenceInDepth, sameOrigin, promptInjection)

fun tipOf(concept: Concept) = postOf(concept, "tip", PostContent.Tip("Tip", "Validate on the server."))
fun mythOf(concept: Concept) = postOf(concept, "myth", PostContent.Myth("Myth", "Myth: safe", "Reality: not safe"))
fun mcqOf(concept: Concept) =
    postOf(concept, "mcq", PostContent.Mcq("Quiz", "Which?", listOf("A", "B", "C", "D"), 1, "Because B."))
fun trueFalseOf(concept: Concept) = postOf(concept, "tf", PostContent.TrueFalse("T/F", "It is so.", true, "It is."))
fun memeOf(concept: Concept) = postOf(concept, "meme", PostContent.Meme("Meme", "meme.jpg", "Caption", emptyList()))

private fun postOf(concept: Concept, suffix: String, content: PostContent) =
    Post(PostId("${concept.id.value}/$suffix"), concept.id, concept.bookSlug, content)

fun postsOf(concept: Concept): List<Post> =
    listOf(tipOf(concept), mythOf(concept), mcqOf(concept), trueFalseOf(concept), memeOf(concept))

fun sourceNoteOf(concept: Concept, html: String) = Note(
    id = concept.sourceNoteId!!,
    bookSlug = concept.bookSlug,
    position = concept.position,
    section = "Section of ${concept.title}",
    part = 1,
    partCount = 1,
    html = html,
    wordCount = 0,
    readingTime = 1.minutes,
)

val defaultSettings = listOf(
    BookSettings(appSec.slug, isActive = true, priority = Priority.MAIN),
    BookSettings(grokking.slug, isActive = true, priority = Priority.NORMAL),
    BookSettings(aiSecurity.slug, isActive = false, priority = Priority.LOW),
)

fun libraryWith(
    readNotes: Set<NoteId> = emptySet(),
    sourceNotes: List<Note> = emptyList(),
    settings: List<BookSettings> = defaultSettings,
) = LearningLibrary(
    books = listOf(appSec, grokking, aiSecurity),
    concepts = allConcepts.shuffled(Random(seed = 7)),
    posts = allConcepts.flatMap(::postsOf),
    sourceNotes = sourceNotes.associateBy(Note::id),
    bookSettings = settings,
    readingOnlyChapters = ReadingOnlyChapters(mapOf(appSec.slug to setOf(2))),
    readingProgress = ReadingProgress(readNotes = readNotes, knownChapterEnds = emptySet()),
)

fun readingEverything(): Set<NoteId> = listOf(appSec, grokking, aiSecurity).map { book -> noteId(book, 9, 99) }.toSet()

val freshState = FeedState(delightAtStep = 99)

fun FeedState.withIntroduced(vararg concepts: Concept, progress: ConceptProgress = introducedProgress()): FeedState =
    copy(conceptProgress = conceptProgress + concepts.associate { it.id to progress })

fun introducedProgress(due: Instant = now.plusSeconds(86_400)) =
    ConceptProgress(introducedAtStep = 1, card = MemoryCard.new(now).copy(due = due), lastShownStep = 1)

fun FeedState.withSeen(vararg posts: Post, atStep: Int = 1): FeedState =
    copy(seenPosts = seenPosts + posts.associate { it.id to atStep })

fun FeedState.afterShowing(vararg posts: Post): FeedState = posts.fold(this) { state, post ->
    val step = state.step + 1
    state.copy(
        step = step,
        seenPosts = state.seenPosts + (post.id to step),
        history = state.history + historyEntryOf(post, step),
    )
}

fun historyEntryOf(post: Post, step: Int, source: CandidateSource = CandidateSource.ANGLE) =
    FeedHistoryEntry(step, post.id, post.conceptId, post.format, post.role, source)
