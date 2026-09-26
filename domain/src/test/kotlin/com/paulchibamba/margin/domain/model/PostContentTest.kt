package com.paulchibamba.margin.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PostContentTest {

    private val mcq = PostContent.Mcq(
        title = "Stored XSS",
        question = "Which control stops stored XSS?",
        options = listOf("Input length limit", "Output encoding", "HTTPS", "Block script tags"),
        answerIndex = 1,
        explanation = "Encoding at output stops every vector.",
    )

    @Test
    fun `an mcq counts the words of its title, question and options`() {
        assertEquals(2 + 5 + 3 + 2 + 1 + 3, mcq.wordCount())
    }

    @Test
    fun `a code example counts its title, caption and code`() {
        val codeExample = PostContent.CodeExample(
            title = "Bind it",
            code = "db.query(\n  \"SELECT * FROM users WHERE id = ?\", id)",
            caption = "Parameters keep data as data.",
        )

        assertEquals(2 + 5 + 10, codeExample.wordCount())
    }

    @Test
    fun `a code example without a caption counts only its title and code`() {
        val codeExample = PostContent.CodeExample(title = "Bind it", code = "db.query(sql, id)", caption = null)

        assertEquals(2 + 2, codeExample.wordCount())
    }

    @Test
    fun `a recall post does not count its hidden answer`() {
        val recall = PostContent.Recall(title = "Factors", question = "Name the three factors", answer = "know have are")

        assertEquals(1 + 4, recall.wordCount())
    }

    @Test
    fun `every post content reports its own format`() {
        assertEquals(Format.MCQ, mcq.format)
        assertEquals(Format.MYTH, PostContent.Myth("Breaches", "Myth: safe", "Reality: not").format)
    }

    @Test
    fun `a choice question knows which option is correct`() {
        assertTrue(mcq.isCorrect(1))
        assertFalse(mcq.isCorrect(3))
    }

    @Test
    fun `a choice question rejects an answer outside its options`() {
        assertFailsWith<IllegalArgumentException> { mcq.copy(answerIndex = 4) }
    }

    @Test
    fun `a carousel needs at least one slide`() {
        assertFailsWith<IllegalArgumentException> { PostContent.Carousel(title = "Empty", slides = emptyList()) }
    }

    @Test
    fun `a post takes its format and role from its content`() {
        val post = Post(PostId("c/abc123"), ConceptId("c"), BookSlug("book"), mcq)

        assertEquals(Format.MCQ, post.format)
        assertEquals(PostRole.TEST, post.role)
    }
}
