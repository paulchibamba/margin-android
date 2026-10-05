package com.paulchibamba.margin.domain.progress

internal object FactsOnlyCheck {
    private val NUMBER = Regex("""\d+""")
    private val NUMBER_WORDS = listOf(
        "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten", "eleven", "twelve", "thirteen",
        "fourteen", "fifteen", "sixteen", "seventeen", "eighteen", "nineteen", "twenty",
    ).mapIndexed { index, word -> Regex("""\b$word\b""", RegexOption.IGNORE_CASE) to (index + 2).toString() }

    fun hasOnlyFactNumbers(text: String, facts: Map<String, String>): Boolean {
        val factNumbers = facts.values.flatMap(::numbersIn).toSet()
        return numbersIn(text).all { number -> number in factNumbers }
    }

    fun hasOnlyFactTitles(text: String, facts: Map<String, String>, titles: Set<String>): Boolean {
        val factText = facts.values.joinToString("\n").lowercase()
        return titles
            .filter { title -> title.isNotBlank() && text.contains(title, ignoreCase = true) }
            .all { title -> title.lowercase() in factText }
    }

    private fun numbersIn(text: String): List<String> =
        NUMBER.findAll(text).map { match -> match.value.trimStart('0').ifEmpty { "0" } }.toList() +
            NUMBER_WORDS.filter { (pattern, _) -> pattern.containsMatchIn(text) }.map { (_, number) -> number }
}
