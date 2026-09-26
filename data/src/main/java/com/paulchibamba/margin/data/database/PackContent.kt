package com.paulchibamba.margin.data.database

import com.paulchibamba.margin.data.database.entity.BookEntity
import com.paulchibamba.margin.data.database.entity.ChapterEntity
import com.paulchibamba.margin.data.database.entity.ConceptEntity
import com.paulchibamba.margin.data.database.entity.NoteEntity
import com.paulchibamba.margin.data.database.entity.PostEntity

data class PackContent(
    val books: List<BookEntity>,
    val chapters: List<ChapterEntity>,
    val concepts: List<ConceptEntity>,
    val posts: List<PostEntity>,
    val notes: List<NoteEntity>,
)
