package com.paulchibamba.margin.domain.model

sealed interface CoverSource {
    data class GalleryImage(val uri: String) : CoverSource
}
