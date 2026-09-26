package com.paulchibamba.margin.data.pack

import com.paulchibamba.margin.data.database.entity.BookSettingsEntity
import com.paulchibamba.margin.data.database.entity.ReadingOnlyChapterEntity
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PackMapperTest {

    private val mapper = PackMapper()

    @Test
    fun `a concept's note position is denormalised from its note id`() {
        val concept = mapper.map(packFiles()).content.concepts.first()

        assertEquals("$APPSEC/ch01/n000", concept.noteId)
        assertEquals(1, concept.noteChapter)
        assertEquals(0, concept.noteOrder)
    }

    @Test
    fun `books keep pack order and posts keep their order within the concept`() {
        val content = mapper.map(packFiles()).content

        assertEquals(listOf(APPSEC to 0, GROKKING to 1), content.books.map { it.slug to it.position })
        assertEquals(listOf("tip" to 0, "mcq" to 1), content.posts.take(2).map { it.format to it.position })
    }

    @Test
    fun `a post's json keeps its fields and omits the ones it doesn't have`() {
        val json = mapper.map(packFiles()).content.posts.first { it.format == "mcq" }.json

        assertEquals(mcqPost("$APPSEC/ch01/c0"), PackJson.decodeFromString(PostDto.serializer(), json))
        assertTrue("null" !in json)
        assertTrue("\"answer_index\":1" in json)
    }

    @Test
    fun `settings defaults come from the library, inactive books get normal priority`() {
        val defaults = mapper.map(packFiles()).defaultBookSettings

        assertEquals(
            listOf(BookSettingsEntity(APPSEC, true, "main"), BookSettingsEntity(GROKKING, false, "normal")),
            defaults,
        )
    }

    @Test
    fun `reading-only chapters come from the library`() {
        assertEquals(
            listOf(ReadingOnlyChapterEntity(APPSEC, 2), ReadingOnlyChapterEntity(GROKKING, 1)),
            mapper.map(packFiles()).defaultReadingOnlyChapters,
        )
    }

    @Test
    fun `concepts without a teach or a test post are reported`() {
        val concepts = listOf(
            conceptDto(APPSEC, 1, 0) { id -> listOf(mcqPost(id)) },
            conceptDto(APPSEC, 1, 1) { id -> listOf(tipPost(id)) },
            conceptDto(APPSEC, 1, 2),
        )

        val warnings = mapper.map(packFiles(books = listOf(bookFile(APPSEC, concepts = concepts)))).warnings

        assertEquals(listOf("$APPSEC/ch01/c0"), warnings.conceptsWithoutTeachPost)
        assertEquals(listOf("$APPSEC/ch01/c1"), warnings.conceptsWithoutTestPost)
    }
}
