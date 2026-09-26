package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.model.NoteOutline

data class ReadingHome(val continueNote: NoteOutline?, val books: List<BookReading>)
