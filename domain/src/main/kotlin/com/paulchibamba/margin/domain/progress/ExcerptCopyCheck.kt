package com.paulchibamba.margin.domain.progress

internal object ExcerptCopyCheck {
    const val MAX_COPIED_WORDS = 12
    private val WORD = Regex("""[\p{L}\p{N}']+""")

    fun copiesTooMuch(text: String, excerpt: String): Boolean {
        val excerptRuns = runsOf(wordsOf(excerpt)).toSet()
        return runsOf(wordsOf(text)).any { run -> run in excerptRuns }
    }

    private fun wordsOf(text: String): List<String> = WORD.findAll(text.lowercase()).map { it.value }.toList()

    private fun runsOf(words: List<String>): List<List<String>> = words.windowed(MAX_COPIED_WORDS + 1)
}
