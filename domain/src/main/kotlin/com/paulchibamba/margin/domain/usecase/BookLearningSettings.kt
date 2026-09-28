package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.model.Book
import com.paulchibamba.margin.domain.model.BookSettings
import com.paulchibamba.margin.domain.model.Chapter

data class BookLearningSettings(val book: Book, val settings: BookSettings, val chapters: List<Chapter>)
