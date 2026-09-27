package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.model.Book
import com.paulchibamba.margin.domain.model.Priority

data class BookReading(val book: Book, val tally: NoteTally, val isActive: Boolean, val priority: Priority)
