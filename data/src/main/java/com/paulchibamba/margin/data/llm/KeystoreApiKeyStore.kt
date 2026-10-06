package com.paulchibamba.margin.data.llm

import android.content.SharedPreferences
import com.paulchibamba.margin.domain.llm.ApiKey
import com.paulchibamba.margin.domain.repository.ApiKeyStore
import java.security.GeneralSecurityException
import java.util.Base64
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class KeystoreApiKeyStore(
    private val preferences: SharedPreferences,
    private val box: SecretBox,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ApiKeyStore {
    private val mutex = Mutex()
    private var current: MutableStateFlow<ApiKey?>? = null

    override suspend fun load(): ApiKey? = stored().value

    override fun observe(): Flow<ApiKey?> = flow { emitAll(stored()) }

    override suspend fun save(key: ApiKey) {
        val sealed = withContext(dispatcher) { box.seal(key.value.toByteArray(Charsets.UTF_8)) }
        writeSealed(sealed)
        stored().value = key
    }

    override suspend fun clear() {
        withContext(dispatcher) { preferences.edit().clear().commit() }
        stored().value = null
    }

    private suspend fun stored(): MutableStateFlow<ApiKey?> = mutex.withLock {
        current ?: MutableStateFlow(readKey()).also { current = it }
    }

    private suspend fun readKey(): ApiKey? = withContext(dispatcher) {
        try {
            readSealed()?.let { sealed -> ApiKey.parse(box.open(sealed).toString(Charsets.UTF_8)) }
        } catch (_: GeneralSecurityException) {
            forgetUnreadableKey()
        } catch (_: IllegalArgumentException) {
            forgetUnreadableKey()
        }
    }

    private fun forgetUnreadableKey(): ApiKey? {
        preferences.edit().clear().commit()
        return null
    }

    private fun readSealed(): SealedSecret? {
        val iv = preferences.getString(IV, null) ?: return null
        val ciphertext = preferences.getString(CIPHERTEXT, null) ?: return null
        return SealedSecret(decode(iv), decode(ciphertext))
    }

    private suspend fun writeSealed(sealed: SealedSecret) = withContext(dispatcher) {
        preferences.edit()
            .putString(IV, encode(sealed.iv))
            .putString(CIPHERTEXT, encode(sealed.ciphertext))
            .commit()
    }

    private fun encode(bytes: ByteArray): String = Base64.getEncoder().encodeToString(bytes)

    private fun decode(text: String): ByteArray = Base64.getDecoder().decode(text)

    companion object {
        const val PREFERENCES_NAME = "llm_api_key"
        private const val IV = "iv"
        private const val CIPHERTEXT = "ciphertext"
    }
}
