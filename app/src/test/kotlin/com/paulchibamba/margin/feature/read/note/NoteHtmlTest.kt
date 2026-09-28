package com.paulchibamba.margin.feature.read.note

import com.paulchibamba.margin.domain.feed.appSec
import com.paulchibamba.margin.domain.feed.noteId
import com.paulchibamba.margin.domain.model.Note
import com.paulchibamba.margin.domain.model.NotePosition
import org.junit.Test
import kotlin.test.assertContains
import kotlin.test.assertFalse
import kotlin.time.Duration.Companion.minutes

class NoteHtmlTest {

    @Test
    fun `a note without a heading gets its section as the title`() {
        val html = NoteHtml.documentOf(noteWith("<p>Words.</p>", section = "Reflected vs stored XSS"), "Ch 4 · XSS")

        assertContains(html, "<h1>Reflected vs stored XSS</h1><p>Words.</p>")
    }

    @Test
    fun `a note that starts with a heading keeps it as the title`() {
        val html = NoteHtml.documentOf(noteWith("<h3>Stored XSS</h3><p>Words.</p>"), "Ch 4 · XSS")

        assertFalse("<h1>" in html)
    }

    @Test
    fun `the section and chapter label are escaped`() {
        val html = NoteHtml.documentOf(noteWith("<p>Words.</p>", section = "<script>x</script>"), "Ch 1 · A & B")

        assertContains(html, "<h1>&lt;script&gt;x&lt;/script&gt;</h1>")
        assertContains(html, "Ch 1 · A &amp; B")
    }

    private fun noteWith(html: String, section: String = "Section") = Note(
        id = noteId(appSec, 4, 5),
        bookSlug = appSec.slug,
        position = NotePosition(4, 5),
        section = section,
        part = 1,
        partCount = 1,
        html = html,
        wordCount = 120,
        readingTime = 1.minutes,
    )
}
