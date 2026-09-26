package com.paulchibamba.margin.data.repository

import com.paulchibamba.margin.data.database.MarginDatabase
import com.paulchibamba.margin.data.repository.mapper.ContentMapper
import com.paulchibamba.margin.data.repository.mapper.SettingsMapper
import com.paulchibamba.margin.data.startup.StartupInitializer
import com.paulchibamba.margin.domain.model.Book
import com.paulchibamba.margin.domain.model.BookSlug
import com.paulchibamba.margin.domain.model.Chapter
import com.paulchibamba.margin.domain.model.Concept
import com.paulchibamba.margin.domain.model.Note
import com.paulchibamba.margin.domain.model.NoteId
import com.paulchibamba.margin.domain.model.NoteOutline
import com.paulchibamba.margin.domain.model.Post
import com.paulchibamba.margin.domain.repository.ContentRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomContentRepository @Inject constructor(
    private val database: MarginDatabase,
    private val startup: StartupInitializer,
) : ContentRepository {
    private val mutex = Mutex()
    private var cache: CachedContent? = null

    override suspend fun books(): List<Book> = cached().books

    override suspend fun concepts(): List<Concept> = cached().concepts

    override suspend fun posts(): List<Post> = cached().posts

    override suspend fun noteOutlines(): List<NoteOutline> = cached().outlines

    override suspend fun chapters(): List<Chapter> {
        startup.ensureImported()
        val readingOnly = SettingsMapper.readingOnly(database.settingsDao().readingOnlyChapters().first())
        return database.contentDao().chapters().map { chapter ->
            ContentMapper.chapter(chapter, readingOnly.contains(BookSlug(chapter.bookSlug), chapter.number))
        }
    }

    override suspend fun notes(ids: Collection<NoteId>): List<Note> {
        startup.ensureImported()
        return ids.map(NoteId::value).chunked(SQLITE_VARIABLE_LIMIT)
            .flatMap { chunk -> database.contentDao().notes(chunk) }
            .map(ContentMapper::note)
    }

    override suspend fun note(id: NoteId): Note? {
        startup.ensureImported()
        return database.contentDao().note(id.value)?.let(ContentMapper::note)
    }

    private suspend fun cached(): CachedContent = mutex.withLock {
        cache ?: load().also { cache = it }
    }

    private suspend fun load(): CachedContent {
        startup.ensureImported()
        val dao = database.contentDao()
        val concepts = dao.concepts().map(ContentMapper::concept)
        val bookOfConcept = concepts.associate { it.id.value to it.bookSlug }
        return CachedContent(
            books = dao.books().map(ContentMapper::book),
            concepts = concepts,
            posts = dao.posts().mapNotNull { post ->
                bookOfConcept[post.conceptId]?.let { book -> ContentMapper.post(post, book) }
            },
            outlines = dao.noteOutlines().map(ContentMapper::outline),
        )
    }

    private data class CachedContent(
        val books: List<Book>,
        val concepts: List<Concept>,
        val posts: List<Post>,
        val outlines: List<NoteOutline>,
    )

    private companion object {
        const val SQLITE_VARIABLE_LIMIT = 900
    }
}
