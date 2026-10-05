package com.paulchibamba.margin.domain.progress.rules

internal object ChapterName {
    private val NUMBER_PREFIX =
        Regex("""^\s*(chapter\s+\d+\s*[:.\-–—]?|\d+[:.\-–—]?\s)\s*""", RegexOption.IGNORE_CASE)

    fun of(title: String): String = title.replace(NUMBER_PREFIX, "").trim().ifEmpty { title }
}
