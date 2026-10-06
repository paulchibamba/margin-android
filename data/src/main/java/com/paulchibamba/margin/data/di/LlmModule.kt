package com.paulchibamba.margin.data.di

import android.content.Context
import com.paulchibamba.margin.data.llm.KeystoreApiKeyStore
import com.paulchibamba.margin.data.llm.KeystoreSecretBox
import com.paulchibamba.margin.data.llm.OpenAiClient
import com.paulchibamba.margin.data.llm.RoomLlmLedger
import com.paulchibamba.margin.data.llm.RoomLlmSettingsRepository
import com.paulchibamba.margin.domain.llm.LlmClient
import com.paulchibamba.margin.domain.repository.ApiKeyStore
import com.paulchibamba.margin.domain.repository.LlmLedger
import com.paulchibamba.margin.domain.repository.LlmSettingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LlmModule {

    @Provides
    @Singleton
    fun apiKeyStore(@ApplicationContext context: Context): ApiKeyStore = KeystoreApiKeyStore(
        context.getSharedPreferences(KeystoreApiKeyStore.PREFERENCES_NAME, Context.MODE_PRIVATE),
        KeystoreSecretBox(),
    )

    @Provides
    fun llmClient(): LlmClient = OpenAiClient()

    @Provides
    fun llmLedger(ledger: RoomLlmLedger): LlmLedger = ledger

    @Provides
    fun llmSettingsRepository(repository: RoomLlmSettingsRepository): LlmSettingsRepository = repository
}
