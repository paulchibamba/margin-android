package com.paulchibamba.margin.feature.read.cover

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object CoverSearchUrl {
    private const val IMAGE_SEARCH = "https://duckduckgo.com/?iax=images&ia=images&kp=1&q="

    fun of(bookTitle: String): String =
        IMAGE_SEARCH + URLEncoder.encode("$bookTitle book cover", StandardCharsets.UTF_8.name())
}
