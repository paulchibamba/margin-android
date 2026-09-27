package com.paulchibamba.margin.feature.feed.post

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import androidx.test.platform.app.InstrumentationRegistry
import com.paulchibamba.margin.data.pack.AndroidAssetSource
import com.paulchibamba.margin.data.pack.BookFileDto
import com.paulchibamba.margin.data.pack.ConceptDto
import com.paulchibamba.margin.data.pack.NoteDto
import com.paulchibamba.margin.data.pack.PackReader
import com.paulchibamba.margin.data.repository.mapper.PostContentMapper
import com.paulchibamba.margin.designsystem.Skins
import com.paulchibamba.margin.domain.feed.CandidateSource
import com.paulchibamba.margin.domain.model.Format
import com.paulchibamba.margin.domain.model.NoteId
import com.paulchibamba.margin.domain.model.PostContent
import com.paulchibamba.margin.domain.model.PostRole
import com.paulchibamba.margin.domain.usecase.ReadingAhead
import com.paulchibamba.margin.feature.feed.FeedPage
import com.paulchibamba.margin.feature.feed.FeedPostPage
import com.paulchibamba.margin.feature.feed.FeedPreviewData
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.minutes

class TeachPostFitTest {

    @get:Rule
    val compose = createComposeRule()

    private val books: List<BookFileDto> by lazy {
        val assets = InstrumentationRegistry.getInstrumentation().targetContext.assets
        PackReader(AndroidAssetSource(assets)).read().books
    }

    @Test
    fun `every tip fits at 360 dp`() = assertEveryPostFits(Format.TIP)

    @Test
    fun `every fact fits at 360 dp`() = assertEveryPostFits(Format.FACT)

    @Test
    fun `every analogy fits at 360 dp`() = assertEveryPostFits(Format.ANALOGY)

    @Test
    fun `every code example fits at 360 dp`() = assertEveryPostFits(Format.CODE_EXAMPLE)

    @Test
    fun `every meme fits at 360 dp`() = assertEveryPostFits(Format.MEME)

    @Test
    fun `every from-the-book post fits at 360 dp`() = assertEveryPageFits(sourcePages())

    @Test
    fun `every teach post fits at 360 dp as a preview`() = assertEveryPageFits(previewPages())

    private fun assertEveryPostFits(format: Format) =
        assertEveryPageFits(packPages().filter { it.item.post.format == format })

    private fun assertEveryPageFits(pages: List<FeedPage>) {
        assertTrue(pages.isNotEmpty(), "The pack has no posts to check")
        var current by mutableStateOf(pages.first())
        compose.setContent {
            Box(Modifier.requiredSize(360.dp, 703.dp)) {
                FeedPostPage(current, 7, null, {}, {}, {}, {}, {})
            }
        }
        pages.forEach { page ->
            current = page
            compose.waitForIdle()
            assertTrue(overflowOfBody() == 0f, "${page.item.post.content.title} overflows by ${overflowOfBody()} px")
        }
    }

    private fun overflowOfBody(): Float {
        val config = compose.onNodeWithTag(POST_BODY_TAG).fetchSemanticsNode().config
        return config.getOrNull(SemanticsProperties.VerticalScrollAxisRange)?.maxValue?.invoke() ?: 0f
    }

    private fun packPages(): List<FeedPage> = books.flatMap { book ->
        book.concepts.flatMap { concept ->
            concept.posts.map { post -> pageOf(book, concept, PostContentMapper.fromDto(post)) }
        }
    }

    private fun previewPages(): List<FeedPage> = packPages()
        .filter { it.item.post.role == PostRole.TEACH }
        .map { page ->
            val ahead = ReadingAhead(NoteId("book/ch01/n001"), noteCount = 4, readingTime = 12.minutes)
            page.copy(
                item = page.item.copy(source = CandidateSource.PREVIEW),
                context = page.context.copy(readingAhead = ahead),
            )
        }

    private fun sourcePages(): List<FeedPage> = books.flatMap { book ->
        book.concepts.mapNotNull { concept ->
            val note = book.notes.firstOrNull { it.id == concept.noteId } ?: return@mapNotNull null
            pageOf(book, concept, PostContent.Source(note.section, excerptOf(note), NoteId(note.id)))
        }
    }

    private fun pageOf(book: BookFileDto, concept: ConceptDto, content: PostContent): FeedPage {
        val preview = FeedPreviewData.page(Skins.Paper, content, CandidateSource.NEW, readingAhead = null)
        val chapterTitle = book.chapters.firstOrNull { it.index == concept.chapter }?.title.orEmpty()
        val context = preview.context.copy(
            conceptTitle = concept.title,
            bookTitle = book.title,
            chapterNumber = concept.chapter,
            chapterTitle = chapterTitle,
        )
        return preview.copy(context = context)
    }

    private fun excerptOf(note: NoteDto): String =
        note.html.replace(Regex("<[^>]*>"), " ").split(Regex("""\s+""")).filter(String::isNotBlank)
            .take(EXCERPT_WORDS).joinToString(" ")

    private companion object {
        const val EXCERPT_WORDS = 90
    }
}
