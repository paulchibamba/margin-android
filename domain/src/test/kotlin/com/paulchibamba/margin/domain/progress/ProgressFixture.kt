package com.paulchibamba.margin.domain.progress

import com.paulchibamba.margin.domain.actions.ActionLogEntry
import com.paulchibamba.margin.domain.actions.PostAction
import com.paulchibamba.margin.domain.feed.CandidateSource
import com.paulchibamba.margin.domain.feed.ConceptProgress
import com.paulchibamba.margin.domain.feed.FeedState
import com.paulchibamba.margin.domain.feed.ReviewLogEntry
import com.paulchibamba.margin.domain.memory.CardState
import com.paulchibamba.margin.domain.memory.MemoryCard
import com.paulchibamba.margin.domain.memory.Rating
import com.paulchibamba.margin.domain.model.Book
import com.paulchibamba.margin.domain.model.BookSlug
import com.paulchibamba.margin.domain.model.Chapter
import com.paulchibamba.margin.domain.model.Concept
import com.paulchibamba.margin.domain.model.ConceptId
import com.paulchibamba.margin.domain.model.Format
import com.paulchibamba.margin.domain.model.NoteId
import com.paulchibamba.margin.domain.model.NoteOutline
import com.paulchibamba.margin.domain.model.NotePosition
import com.paulchibamba.margin.domain.model.Post
import com.paulchibamba.margin.domain.model.PostContent
import com.paulchibamba.margin.domain.model.PostId
import com.paulchibamba.margin.domain.progression.ReadingOnlyChapters
import com.paulchibamba.margin.domain.progression.ReadingState
import com.paulchibamba.margin.domain.tracking.Event
import com.paulchibamba.margin.domain.tracking.LoggedEvent
import java.time.Duration
import java.time.Instant
import java.time.ZoneOffset
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

val NOW: Instant = Instant.parse("2026-10-05T12:00:00Z")

fun daysAgo(days: Long): Instant = NOW.minus(Duration.ofDays(days))

val appSec = Book(BookSlug("appsec"), "Alice and Bob Learn Application Security")
val grokking = Book(BookSlug("grokking"), "Grokking Web Application Security")

val fundamentals = Chapter(appSec.slug, 1, "Security Fundamentals", isReadingOnly = false)
val inputHandling = Chapter(appSec.slug, 2, "Input Handling", isReadingOnly = false)
val browsers = Chapter(grokking.slug, 1, "Browsers", isReadingOnly = false)

val cia = conceptOf(fundamentals, 0, "The CIA Triad", "CIA")
val leastPrivilege = conceptOf(fundamentals, 1, "Least Privilege", "Principles")
val needToKnow = conceptOf(fundamentals, 2, "Least Privilege & Need to Know", "Access")
val defenceInDepth = conceptOf(fundamentals, 3, "Defence in Depth", "Principles")
val sqlInjection = conceptOf(inputHandling, 0, "SQL Injection", "Injection")
val parameterizedQueries = conceptOf(inputHandling, 1, "Parameterized Queries", "Injection")
val storedXss = conceptOf(browsers, 0, "Stored Scripting Attacks", "XSS")
val privilegeInBrowsers = conceptOf(browsers, 1, "Browser Privilege Separation", "Sandbox")

val allConcepts = listOf(
    cia, leastPrivilege, needToKnow, defenceInDepth, sqlInjection, parameterizedQueries, storedXss, privilegeInBrowsers,
)

fun noteIdOf(chapter: Chapter, order: Int): NoteId {
    val chapterNumber = chapter.number.toString().padStart(2, '0')
    return NoteId("${chapter.bookSlug.value}/ch$chapterNumber/n${order.toString().padStart(3, '0')}")
}

fun conceptOf(chapter: Chapter, order: Int, title: String, section: String) = Concept(
    id = ConceptId("${chapter.bookSlug.value}/ch${chapter.number}/c$order"),
    bookSlug = chapter.bookSlug,
    chapter = chapter.number,
    order = order,
    title = title,
    summary = "Summary of $title.",
    section = section,
    sourceNoteId = noteIdOf(chapter, order + 1),
)

fun factPostOf(concept: Concept) = Post(
    PostId("${concept.id.value}/fact"),
    concept.id,
    concept.bookSlug,
    PostContent.Fact("Fact on ${concept.title}", "…"),
)

fun mcqPostOf(concept: Concept) = Post(
    PostId("${concept.id.value}/mcq"),
    concept.id,
    concept.bookSlug,
    PostContent.Mcq("Quiz on ${concept.title}", "Which?", listOf("A", "B"), 0, "Because A."),
)

fun outlinesOf(chapter: Chapter, notes: Int) = (0..notes).map { order ->
    val position = NotePosition(chapter.number, order)
    NoteOutline(noteIdOf(chapter, order), chapter.bookSlug, position, "Section $order", 1.minutes)
}

fun rememberedCard(lastReview: Instant) = MemoryCard.new(lastReview).copy(
    state = CardState.REVIEW,
    scheduledDays = 30,
    lastReview = lastReview,
)

class ProgressFixture {
    var now: Instant = NOW
    var state = FeedState(delightAtStep = 99)
    var readNotes: Set<NoteId> = emptySet()
    var history = RewardHistory.Empty
    var readingOnlyChapters = ReadingOnlyChapters.None
    val reviews = mutableListOf<ReviewLogEntry>()
    val actions = mutableListOf<ActionLogEntry>()
    val firstSeen = mutableMapOf<PostId, Instant>()
    val events = mutableListOf<LoggedEvent>()

    fun introduce(vararg concepts: Concept, on: Instant = now) = concepts.forEach { concept ->
        state = state.withProgress(concept.id, ConceptProgress(introducedAtStep = 1, card = MemoryCard.new(on)))
        firstSeen[factPostOf(concept).id] = on
    }

    fun remember(vararg concepts: Concept, lastReview: Instant = daysAgo(1)) = concepts.forEach { concept ->
        if (!state.isIntroduced(concept)) introduce(concept, on = daysAgo(40))
        state = state.withProgress(concept.id, state.progressOf(concept).copy(card = rememberedCard(lastReview)))
    }

    fun review(concept: Concept, rating: Rating, at: Instant) {
        reviews += ReviewLogEntry(at, concept.id, mcqPostOf(concept).id, rating, CardState.REVIEW, 1.0, 1.0, null)
    }

    fun lost(concept: Concept, at: Instant) {
        actions += ActionLogEntry(at, step = 1, factPostOf(concept).id, concept.id, PostAction.LOST)
    }

    fun see(post: Post) {
        state = state.copy(seenPosts = state.seenPosts + (post.id to 1))
    }

    fun log(event: Event, at: Instant = now.minusSeconds(60)) {
        events += LoggedEvent(at, sessionId = null, event)
    }

    fun glance(concept: Concept, times: Int, activeMs: Long, at: Instant = now.minusSeconds(60)) = repeat(times) {
        val post = factPostOf(concept)
        log(Event.PostImpression(post.id, concept.id, Format.FACT, "Paper", CandidateSource.REVIEW, 1, false), at)
        log(Event.PostExposure(post.id, activeMs.milliseconds, 0.seconds, 20, 5.seconds, false), at)
    }

    fun sources() = ProgressSources(
        books = listOf(appSec, grokking),
        chapters = listOf(fundamentals, inputHandling, browsers),
        concepts = allConcepts,
        posts = allConcepts.flatMap { concept -> listOf(factPostOf(concept), mcqPostOf(concept)) },
        noteOutlines = outlinesOf(fundamentals, 4) + outlinesOf(inputHandling, 2) + outlinesOf(browsers, 2),
        activeBooks = setOf(appSec.slug, grokking.slug),
        readingOnlyChapters = readingOnlyChapters,
        reading = ReadingState(readNotes = readNotes),
        feedState = state,
        reviews = reviews.toList(),
        actions = actions.toList(),
        firstSeen = firstSeen.toMap(),
        events = events.toList(),
        history = history,
    )

    fun facts(): ProgressFacts = ProgressFactsCalculator().factsOf(sources(), now, ZoneOffset.UTC)

    fun seedsOf(rule: RewardRule): List<RewardSeed> = rule.seedsFrom(facts(), history)
}

fun pastReward(
    kind: RewardKind,
    concepts: List<Concept> = emptyList(),
    facts: Map<String, String> = emptyMap(),
    createdAt: Instant = daysAgo(1),
    shownAt: Instant? = null,
    note: NoteId? = null,
    title: String = "An earlier ${kind.key}",
) = PastReward(kind, concepts.map(Concept::id), note, facts, title, createdAt, shownAt)
