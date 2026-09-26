package com.paulchibamba.margin.domain.repository

import com.paulchibamba.margin.domain.model.Book
import com.paulchibamba.margin.domain.model.Chapter
import com.paulchibamba.margin.domain.model.Concept
import com.paulchibamba.margin.domain.model.Note
import com.paulchibamba.margin.domain.model.NoteId
import com.paulchibamba.margin.domain.model.NoteOutline
import com.paulchibamba.margin.domain.model.Post

interface ContentRepository {
    suspend fun books(): List<Book>
    suspend fun chapters(): List<Chapter>
    suspend fun concepts(): List<Concept>
    suspend fun posts(): List<Post>
    suspend fun noteOutlines(): List<NoteOutline>
    suspend fun notes(ids: Collection<NoteId>): List<Note>
    suspend fun note(id: NoteId): Note?
}
