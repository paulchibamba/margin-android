package com.paulchibamba.margin.feature.feed.post

data class TipText(val body: String?, val callout: String) {

    companion object {
        private val SENTENCE_END = Regex("""(?<=[.!?])\s+(?=\p{Lu})""")

        fun of(text: String): TipText {
            val sentences = text.trim().split(SENTENCE_END)
            if (sentences.size < 2) return TipText(body = null, callout = text.trim())
            return TipText(body = sentences.dropLast(1).joinToString(" "), callout = sentences.last())
        }
    }
}
