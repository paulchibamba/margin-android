package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.repository.ApiKeyStore
import javax.inject.Inject

class RemoveApiKey @Inject constructor(private val keys: ApiKeyStore) {

    suspend operator fun invoke() {
        keys.clear()
    }
}
