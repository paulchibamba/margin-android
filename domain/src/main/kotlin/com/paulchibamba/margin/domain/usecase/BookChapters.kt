package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.model.Book
import com.paulchibamba.margin.domain.rewards.BookCompletion

data class BookChapters(val book: Book, val chapters: List<ChapterReading>, val completion: BookCompletion)
