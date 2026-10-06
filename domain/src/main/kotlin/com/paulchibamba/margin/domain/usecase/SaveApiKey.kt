package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.llm.ApiKey
import com.paulchibamba.margin.domain.repository.ApiKeyStore
import javax.inject.Inject

class SaveApiKey @Inject constructor(private val keys: ApiKeyStore) {

    suspend operator fun invoke(pasted: String): Boolean {
        val key = ApiKey.parse(pasted) ?: return false
        keys.save(key)
        return true
    }
}
