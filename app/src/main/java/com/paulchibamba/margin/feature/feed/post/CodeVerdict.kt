package com.paulchibamba.margin.feature.feed.post

enum class CodeVerdict {
    SAFE,
    UNSAFE;

    companion object {
        private val UNSAFE_WORDS = Regex("""\b(unsafe|insecure|vulnerable|injectable|dangerous|bad|broken)\b""",
            RegexOption.IGNORE_CASE)
        private val SAFE_WORDS = Regex("""\b(safe|secure|parameteri[sz]|escap|validat|integrity|restrict|lock|block)""",
            RegexOption.IGNORE_CASE)

        fun of(title: String, caption: String?): CodeVerdict? {
            val text = listOfNotNull(title, caption).joinToString(" ")
            return when {
                UNSAFE_WORDS.containsMatchIn(text) -> UNSAFE
                SAFE_WORDS.containsMatchIn(text) -> SAFE
                else -> null
            }
        }
    }
}
