package com.paulchibamba.margin.domain.model

@JvmInline
value class NoteId(val value: String) {

    val bookSlug: BookSlug
        get() = BookSlug(value.substringBefore(SEPARATOR))

    fun position(): NotePosition? {
        val match = POSITION_PATTERN.find(value) ?: return null
        val (chapter, order) = match.destructured
        return NotePosition(chapter.toInt(), order.toInt())
    }

    private companion object {
        const val SEPARATOR = "/"
        val POSITION_PATTERN = Regex("""/ch(\d+)/n(\d+)$""")
    }
}
