package com.paulchibamba.margin.domain.feed

internal object NoteExcerpt {

    private const val WORD_LIMIT = 90
    private val TAG = Regex("<[^>]*>")
    private val WHITESPACE = Regex("""\s+""")
    private val ENTITIES = mapOf("&lt;" to "<", "&gt;" to ">", "&quot;" to "\"", "&#39;" to "'", "&amp;" to "&")

    fun of(html: String): String = plainText(html).split(' ').take(WORD_LIMIT).joinToString(" ")

    private fun plainText(html: String): String =
        decodeEntities(html.replace(TAG, " ")).replace(WHITESPACE, " ").trim()

    private fun decodeEntities(text: String): String =
        ENTITIES.entries.fold(text) { decoded, (entity, character) -> decoded.replace(entity, character) }
}
