package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.model.Book

data class BookReading(val book: Book, val tally: NoteTally)
