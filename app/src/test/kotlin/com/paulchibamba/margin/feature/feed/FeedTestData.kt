package com.paulchibamba.margin.feature.feed

import com.paulchibamba.margin.domain.feed.CandidateSource
import com.paulchibamba.margin.domain.feed.FeedItem
import com.paulchibamba.margin.domain.feed.ranking.ScoreBreakdown
import com.paulchibamba.margin.domain.model.BookSlug
import com.paulchibamba.margin.domain.model.ConceptId
import com.paulchibamba.margin.domain.model.NoteId
import com.paulchibamba.margin.domain.model.Post
import com.paulchibamba.margin.domain.model.PostContent
import com.paulchibamba.margin.domain.model.PostId
import com.paulchibamba.margin.domain.rewards.BookCompletion
import com.paulchibamba.margin.domain.usecase.PostContext

private val book = BookSlug("alice-bob-appsec")

fun tipPost(index: Int) = Post(
    id = PostId("post-$index"),
    conceptId = ConceptId("concept-$index"),
    bookSlug = book,
    content = PostContent.Tip("Tip $index", "Validate on the server."),
)

fun mcqPost(index: Int) = Post(
    id = PostId("mcq-$index"),
    conceptId = ConceptId("concept-$index"),
    bookSlug = book,
    content = PostContent.Mcq("Quiz $index", "Which one?", listOf("A", "B", "C", "D"), 1, "Because B."),
)

fun recallPost(index: Int) = Post(
    id = PostId("recall-$index"),
    conceptId = ConceptId("concept-$index"),
    bookSlug = book,
    content = PostContent.Recall("Recall $index", "Name the three factors.", "Know, have, are."),
)

fun itemOf(post: Post) = FeedItem(
    post = post,
    source = CandidateSource.NEW,
    score = ScoreBreakdown(emptyMap()),
    rank = 1,
    poolSize = 1,
    wasExploration = false,
    appliedFilters = emptyList(),
    memory = null,
)

fun contextOf(post: Post) = PostContext(
    conceptTitle = "Concept ${post.conceptId.value}",
    bookTitle = "Alice and Bob Learn Application Security",
    chapterNumber = 1,
    chapterTitle = "CHAPTER 1: Foundations",
    completion = BookCompletion(book, introduced = 1, remembered = 0, total = 10),
    sourceNote = NoteId("alice-bob-appsec/ch01/n001"),
)
