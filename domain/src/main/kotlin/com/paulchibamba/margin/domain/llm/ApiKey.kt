package com.paulchibamba.margin.domain.llm

@JvmInline
value class ApiKey(val value: String) {

    init {
        require(isWellFormed(value)) { "An API key is one word with no spaces" }
    }

    val masked: String get() = MASK + value.takeLast(VISIBLE_CHARACTERS)

    override fun toString(): String = "ApiKey($masked)"

    companion object {
        private const val MASK = "••••"
        private const val VISIBLE_CHARACTERS = 4

        fun parse(text: String): ApiKey? = text.trim().takeIf(::isWellFormed)?.let(::ApiKey)

        private fun isWellFormed(text: String): Boolean = text.isNotEmpty() && text.none(Char::isWhitespace)
    }
}
