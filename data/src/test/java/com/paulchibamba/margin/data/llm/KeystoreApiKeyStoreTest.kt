package com.paulchibamba.margin.data.llm

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.paulchibamba.margin.domain.llm.ApiKey
import java.security.GeneralSecurityException
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class KeystoreApiKeyStoreTest {

    private val preferences = ApplicationProvider.getApplicationContext<Context>()
        .getSharedPreferences(KeystoreApiKeyStore.PREFERENCES_NAME, Context.MODE_PRIVATE)
    private val box = ReversingBox()

    @Test
    fun `there is no key until one is saved`() = runTest {
        assertNull(store().load())
    }

    @Test
    fun `a saved key is read back by a fresh store`() = runTest {
        store().save(ApiKey(FAKE_KEY))

        assertEquals(ApiKey(FAKE_KEY), store().load())
    }

    @Test
    fun `only the sealed key is written to preferences`() = runTest {
        store().save(ApiKey(FAKE_KEY))

        val written = preferences.all.values.joinToString()
        assertFalse(written.contains(FAKE_KEY))
        assertFalse(written.contains("sk-test"))
    }

    @Test
    fun `observers see the key saved and cleared`() = runTest {
        val store = store()
        store.save(ApiKey(FAKE_KEY))
        assertEquals(ApiKey(FAKE_KEY), store.observe().first())

        store.clear()

        assertNull(store.observe().first())
        assertNull(store().load())
    }

    @Test
    fun `a key that can't be opened any more is forgotten`() = runTest {
        store().save(ApiKey(FAKE_KEY))
        box.isBroken = true

        assertNull(store().load())
        assertEquals(emptyMap(), preferences.all)
    }

    private fun store() = KeystoreApiKeyStore(preferences, box, Dispatchers.Unconfined)

    private class ReversingBox : SecretBox {
        var isBroken = false

        override fun seal(plain: ByteArray) = SealedSecret(byteArrayOf(1, 2, 3), plain.reversedArray())

        override fun open(sealed: SealedSecret): ByteArray {
            if (isBroken) throw GeneralSecurityException("Key permanently invalidated")
            return sealed.ciphertext.reversedArray()
        }
    }

    private companion object {
        const val FAKE_KEY = "sk-test-0123456789abcdefWXYZ"
    }
}
