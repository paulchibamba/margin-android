package com.paulchibamba.margin.data.llm

import com.paulchibamba.margin.data.database.MarginDatabase
import com.paulchibamba.margin.data.database.MetaKey
import com.paulchibamba.margin.data.database.entity.MetaEntity
import com.paulchibamba.margin.domain.llm.LlmModel
import com.paulchibamba.margin.domain.llm.LlmSettings
import com.paulchibamba.margin.domain.llm.MicroDollars
import com.paulchibamba.margin.domain.repository.LlmSettingsRepository
import com.paulchibamba.margin.domain.tracking.Event
import com.paulchibamba.margin.domain.tracking.EventRecorder
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first

class RoomLlmSettingsRepository @Inject constructor(
    private val database: MarginDatabase,
    private val events: EventRecorder,
) : LlmSettingsRepository {
    private val metaDao get() = database.metaDao()

    override suspend fun settings(): LlmSettings = observeSettings().first()

    override fun observeSettings(): Flow<LlmSettings> = combine(
        metaDao.observe(MetaKey.LLM_MODEL_BAKE),
        metaDao.observe(MetaKey.LLM_DAILY_CAP_MICROS),
        metaDao.observe(MetaKey.SEND_EXCERPTS),
        ::settingsOf,
    )

    override suspend fun saveSettings(settings: LlmSettings) {
        putSetting(MetaKey.LLM_MODEL_BAKE, settings.bakeModel.id)
        putSetting(MetaKey.LLM_DAILY_CAP_MICROS, settings.dailyCap.value.toString())
        putSetting(MetaKey.SEND_EXCERPTS, settings.isSendingExcerpts.toString())
    }

    private fun settingsOf(model: String?, dailyCap: String?, sendExcerpts: String?): LlmSettings {
        val defaults = LlmSettings()
        return LlmSettings(
            bakeModel = model?.let(::LlmModel) ?: defaults.bakeModel,
            dailyCap = dailyCap?.toLongOrNull()?.let(::MicroDollars) ?: defaults.dailyCap,
            isSendingExcerpts = sendExcerpts?.toBooleanStrictOrNull() ?: defaults.isSendingExcerpts,
        )
    }

    private suspend fun putSetting(key: String, value: String) {
        val old = metaDao.get(key)
        if (old == value) return
        metaDao.put(listOf(MetaEntity(key, value)))
        events.record(Event.SettingChanged(key, old, value))
    }
}
