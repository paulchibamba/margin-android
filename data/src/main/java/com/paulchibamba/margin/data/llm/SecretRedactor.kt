package com.paulchibamba.margin.data.llm

import com.paulchibamba.margin.domain.llm.ApiKey

object SecretRedactor {

    private val keyLike = Regex("""sk-[A-Za-z0-9_*.-]+""")
    private const val REDACTED = "[redacted key]"
    private const val MAX_DETAIL_LENGTH = 300

    fun redact(text: String, key: ApiKey): String =
        text.replace(key.value, REDACTED).replace(keyLike, REDACTED).take(MAX_DETAIL_LENGTH)
}
