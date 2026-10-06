package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.repository.ApiKeyStore
import com.paulchibamba.margin.domain.repository.Clock
import com.paulchibamba.margin.domain.repository.LlmLedger
import com.paulchibamba.margin.domain.repository.LlmSettingsRepository
import com.paulchibamba.margin.domain.time.startOfToday
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class ObserveLlmOverview @Inject constructor(
    private val keys: ApiKeyStore,
    private val settings: LlmSettingsRepository,
    private val ledger: LlmLedger,
    private val clock: Clock,
) {

    operator fun invoke(): Flow<LlmOverview> = combine(
        keys.observe(),
        settings.observeSettings(),
        ledger.observeSpendSince(clock.startOfToday()),
    ) { key, settings, spend -> LlmOverview(key?.masked, settings, spend) }
}
