package com.paulchibamba.margin.feature.feed.post

private val BLANK = Regex("""_{2,}(?:\s+_{2,})*""")

data class BlankSentence(val before: String, val after: String) {

    companion object {
        fun parse(sentence: String): BlankSentence {
            val blank = BLANK.find(sentence) ?: return BlankSentence(sentence.trimEnd() + " ", "")
            return BlankSentence(sentence.substring(0, blank.range.first), sentence.substring(blank.range.last + 1))
        }
    }
}
