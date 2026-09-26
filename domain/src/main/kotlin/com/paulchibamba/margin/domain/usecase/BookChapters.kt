package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.model.Book

data class BookChapters(val book: Book, val chapters: List<ChapterReading>)
