package com.paulchibamba.margin.domain.progress

import kotlin.random.Random

internal object QuotePicker {
    private const val MIN_LENGTH = 40
    private const val MAX_LENGTH = 220
    private val SENTENCE_END = Regex("""(?<=[.?])\s+""")
    private val BANNED_CHARACTERS = setOf('!', '<', '>', '?')

    fun pick(noteText: String, random: Random): String? = candidates(noteText).randomOrNull(random)

    fun candidates(noteText: String): List<String> = sentencesOf(noteText)
        .filter { sentence -> sentence.length in MIN_LENGTH..MAX_LENGTH && sentence.endsWith('.') }
        .filter { sentence -> sentence.none(BANNED_CHARACTERS::contains) && sentence.first().isUpperCase() }

    fun sentencesOf(noteText: String): List<String> =
        noteText.split(SENTENCE_END).map(String::trim).filter(String::isNotEmpty)
}
