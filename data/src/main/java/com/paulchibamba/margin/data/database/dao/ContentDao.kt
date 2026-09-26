package com.paulchibamba.margin.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.paulchibamba.margin.data.database.NoteOutlineRow
import com.paulchibamba.margin.data.database.PackContent
import com.paulchibamba.margin.data.database.entity.BookEntity
import com.paulchibamba.margin.data.database.entity.ChapterEntity
import com.paulchibamba.margin.data.database.entity.ConceptEntity
import com.paulchibamba.margin.data.database.entity.NoteEntity
import com.paulchibamba.margin.data.database.entity.PostEntity

@Dao
abstract class ContentDao {

    @Transaction
    open suspend fun replaceAll(content: PackContent) {
        deleteAllContent()
        insertBooks(content.books)
        insertChapters(content.chapters)
        insertConcepts(content.concepts)
        insertPosts(content.posts)
        insertNotes(content.notes)
    }

    @Query("SELECT * FROM book ORDER BY position")
    abstract suspend fun books(): List<BookEntity>

    @Query("SELECT * FROM chapter ORDER BY bookSlug, number")
    abstract suspend fun chapters(): List<ChapterEntity>

    @Query("SELECT * FROM concept ORDER BY bookSlug, chapter, `order`")
    abstract suspend fun concepts(): List<ConceptEntity>

    @Query("SELECT * FROM post ORDER BY conceptId, position")
    abstract suspend fun posts(): List<PostEntity>

    @Query("SELECT * FROM note WHERE bookSlug = :bookSlug ORDER BY chapter, `order`")
    abstract suspend fun notesOf(bookSlug: String): List<NoteEntity>

    @Query("SELECT id, bookSlug, chapter, `order`, section, minutes FROM note ORDER BY bookSlug, chapter, `order`")
    abstract suspend fun noteOutlines(): List<NoteOutlineRow>

    @Query("SELECT * FROM note WHERE id IN (:ids)")
    abstract suspend fun notes(ids: List<String>): List<NoteEntity>

    @Query("SELECT * FROM note WHERE id = :id")
    abstract suspend fun note(id: String): NoteEntity?

    @Insert
    protected abstract suspend fun insertBooks(books: List<BookEntity>)

    @Insert
    protected abstract suspend fun insertChapters(chapters: List<ChapterEntity>)

    @Insert
    protected abstract suspend fun insertConcepts(concepts: List<ConceptEntity>)

    @Insert
    protected abstract suspend fun insertPosts(posts: List<PostEntity>)

    @Insert
    protected abstract suspend fun insertNotes(notes: List<NoteEntity>)

    private suspend fun deleteAllContent() {
        deleteBooks()
        deleteChapters()
        deleteConcepts()
        deletePosts()
        deleteNotes()
    }

    @Query("DELETE FROM book")
    protected abstract suspend fun deleteBooks()

    @Query("DELETE FROM chapter")
    protected abstract suspend fun deleteChapters()

    @Query("DELETE FROM concept")
    protected abstract suspend fun deleteConcepts()

    @Query("DELETE FROM post")
    protected abstract suspend fun deletePosts()

    @Query("DELETE FROM note")
    protected abstract suspend fun deleteNotes()
}
