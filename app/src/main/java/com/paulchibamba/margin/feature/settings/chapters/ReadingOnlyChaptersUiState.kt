package com.paulchibamba.margin.feature.settings.chapters

import com.paulchibamba.margin.domain.usecase.BookLearningSettings

data class ReadingOnlyChaptersUiState(
    val books: List<BookLearningSettings> = emptyList(),
    val isLoading: Boolean = true,
)
