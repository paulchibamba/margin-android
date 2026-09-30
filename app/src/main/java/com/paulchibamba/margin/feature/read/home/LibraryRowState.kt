package com.paulchibamba.margin.feature.read.home

import com.paulchibamba.margin.domain.model.BookSlug
import com.paulchibamba.margin.domain.model.Priority
import com.paulchibamba.margin.domain.usecase.NoteTally

data class LibraryRowState(
    val book: BookSlug,
    val title: String,
    val priority: Priority?,
    val tally: NoteTally,
    val coverPath: String? = null,
)
