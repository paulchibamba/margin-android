package com.paulchibamba.margin.domain.progress

data class ValidationSources(
    val conceptTitles: Set<String> = emptySet(),
    val noteText: String? = null,
    val isModelWritten: Boolean = false,
)
