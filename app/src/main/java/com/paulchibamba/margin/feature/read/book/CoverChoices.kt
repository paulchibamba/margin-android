package com.paulchibamba.margin.feature.read.book

data class CoverChoices(
    val onChooseFromGallery: () -> Unit,
    val onSearchWeb: () -> Unit,
    val onRemove: () -> Unit,
)
