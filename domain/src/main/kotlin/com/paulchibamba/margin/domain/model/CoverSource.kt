package com.paulchibamba.margin.domain.model

sealed interface CoverSource {
    data class GalleryImage(val uri: String) : CoverSource

    data class WebImage(val url: String) : CoverSource
}
