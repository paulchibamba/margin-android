package com.paulchibamba.margin.feature.read.home

import com.paulchibamba.margin.domain.model.BookCover
import com.paulchibamba.margin.domain.model.BookSlug
import com.paulchibamba.margin.domain.usecase.BookReading
import com.paulchibamba.margin.domain.usecase.ContinueNote
import com.paulchibamba.margin.domain.usecase.ReadingHome
import com.paulchibamba.margin.feature.read.progress
import com.paulchibamba.margin.feature.read.ringLabels

data class ReadHomeUiState(
    val streak: Int = 0,
    val rings: List<BookRingState> = emptyList(),
    val continueNote: ContinueNote? = null,
    val library: List<LibraryRowState> = emptyList(),
    val isLoading: Boolean = true,
) {
    companion object {
        fun of(home: ReadingHome, streak: Int, covers: Map<BookSlug, BookCover> = emptyMap()) = ReadHomeUiState(
            streak = streak,
            rings = home.books.zip(ringLabels(home.books.map { it.book.title })) { reading, label ->
                ringOf(reading, label, covers[reading.book.slug])
            },
            continueNote = home.continueNote,
            library = home.books.map { reading -> libraryRowOf(reading, covers[reading.book.slug]) },
            isLoading = false,
        )

        private fun ringOf(reading: BookReading, label: String, cover: BookCover?) = BookRingState(
            book = reading.book.slug,
            label = label,
            progress = reading.tally.progress(),
            isActive = reading.isActive,
            coverPath = cover?.imagePath,
        )

        private fun libraryRowOf(reading: BookReading, cover: BookCover?) = LibraryRowState(
            book = reading.book.slug,
            title = reading.book.title,
            priority = reading.priority.takeIf { reading.isActive },
            tally = reading.tally,
            coverPath = cover?.imagePath,
        )
    }
}
