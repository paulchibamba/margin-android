package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.model.Book
import com.paulchibamba.margin.domain.model.NotePosition

data class BookFrontier(val book: Book, val frontier: NotePosition?)
