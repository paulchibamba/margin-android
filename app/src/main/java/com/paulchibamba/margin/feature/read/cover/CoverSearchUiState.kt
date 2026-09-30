package com.paulchibamba.margin.feature.read.cover

data class CoverSearchUiState(
    val bookTitle: String,
    val searchUrl: String,
    val pendingImageUrl: String? = null,
    val isSaving: Boolean = false,
    val isDone: Boolean = false,
    val isRejected: Boolean = false,
)
